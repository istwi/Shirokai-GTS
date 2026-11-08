#!/usr/bin/env python3
"""Build the narrow hotfix without pretending the recovered project fully compiles."""
from pathlib import Path
import hashlib
import shutil
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]
CACHE = ROOT / '.cache'
BUILD = ROOT / 'build'
BASE = ROOT / 'vendor/GTS-Shirokai-2.6.2-fabric.jar'
TARGET = 'org/pokesplash/gts/UI/CurrencySelection.class'
HELPER = 'org/pokesplash/gts/UI/ReusableSelectionTemplate.class'


def run(*args):
    subprocess.run([str(a) for a in args], cwd=ROOT, check=True)


def verify(path, digest):
    if hashlib.sha256(path.read_bytes()).hexdigest() != digest:
        raise RuntimeError(f'SHA-256 mismatch: {path}')


def dependency(name, url, digest):
    path = CACHE / name
    if not path.exists():
        run('curl', '-fL', '--retry', '2', '--max-time', '120', url, '-o', path)
    verify(path, digest)
    return path


def main():
    CACHE.mkdir(exist_ok=True)
    BUILD.mkdir(exist_ok=True)
    verify(BASE, '3f379ae44b1cf5881031540be1a7db0d1f540ab83ff6f074b0f228c8d058885c')
    ecj = dependency('ecj.jar', 'https://repo.maven.apache.org/maven2/org/eclipse/jdt/ecj/3.40.0/ecj-3.40.0.jar',
                     '05cc22a24e7982970f63a405fc6c820bc80b806f27f3c5a6236fc475f8f7152b')
    asm = dependency('asm.jar', 'https://repo.maven.apache.org/maven2/org/ow2/asm/asm/9.7.1/asm-9.7.1.jar',
                     '8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281')
    guava = dependency('guava.jar', 'https://repo.maven.apache.org/maven2/com/google/guava/guava/33.3.1-jre/guava-33.3.1-jre.jar',
                       '4bf0e2c5af8e4525c96e8fde17a4f7307f97f8478f11c4c8e35a0e3298ae4e90')
    repack = BUILD / 'fabric-api-repack.jar'
    original = BUILD / 'CurrencySelection.original.class'
    patched = BUILD / 'CurrencySelection.class'
    with zipfile.ZipFile(BASE) as z:
        repack.write_bytes(z.read('META-INF/jars/fabric-api-repack-3.1.1-1.21.x.jar'))
        original.write_bytes(z.read(TARGET))
    classes = BUILD / 'classes'
    if classes.exists():
        shutil.rmtree(classes)
    classes.mkdir()
    run('java', '-jar', ecj, '-21', '-proc:none', '-cp', asm, '-d', classes, ROOT / 'tools/PatchCurrencySelection.java')
    run('java', '-jar', ecj, '-21', '-proc:none', '-cp', repack, '-d', classes,
        ROOT / 'patch/src/org/pokesplash/gts/UI/ReusableSelectionTemplate.java')
    run('java', '-cp', f'{classes}:{asm}', 'PatchCurrencySelection', original, patched)

    # Only a type placeholder for compiling the test's Button implementation.
    # Never included in the output mod; the real template library is used in tests.
    stub = BUILD / 'test-stubs/net/minecraft/class_1799.java'
    stub.parent.mkdir(parents=True, exist_ok=True)
    stub.write_text('package net.minecraft; public class class_1799 {}\n')
    screen_stub = stub.with_name('class_3917.java')
    screen_stub.write_text('package net.minecraft; public class class_3917 {}\n')
    run('java', '-jar', ecj, '-21', '-proc:none', '-cp', f'{classes}:{repack}', '-d', classes,
        stub, screen_stub, ROOT / 'tests/SelectionTemplateTest.java')
    run('java', '-Xverify:all', '-cp', f'{classes}:{repack}:{guava}', 'SelectionTemplateTest')

    dist = ROOT / 'dist'
    dist.mkdir(exist_ok=True)
    output = dist / 'GTS-Shirokai-2.6.2-fabric-currency-back-fix.jar'
    with zipfile.ZipFile(BASE) as source, zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as dest:
        for info in source.infolist():
            dest.writestr(info, patched.read_bytes() if info.filename == TARGET else source.read(info.filename))
        dest.writestr(HELPER, (classes / HELPER).read_bytes())
    with zipfile.ZipFile(BASE) as a, zipfile.ZipFile(output) as b:
        assert set(b.namelist()) - set(a.namelist()) == {HELPER}
        assert set(a.namelist()) - set(b.namelist()) == set()
        changed = {n for n in a.namelist() if a.read(n) != b.read(n)}
        assert changed == {TARGET}, changed
        assert b.testzip() is None
    print(f'PASS: only CurrencySelection changed; one helper added\n{output}')
    print('SHA-256:', hashlib.sha256(output.read_bytes()).hexdigest())


if __name__ == '__main__':
    main()

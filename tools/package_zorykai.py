#!/usr/bin/env python3
"""Preserve the existing Zorykai variant: change two visual strings and mod name only."""
import argparse
import json
import struct
import zipfile
from pathlib import Path

LABELS = {
    'GTS Shirokai — escolha a moeda'.encode(): 'GTS Zorykai — escolha a moeda'.encode(),
    'GTS Shirokai — confirmar anúncio'.encode(): 'GTS Zorykai — confirmar anúncio'.encode(),
}
TARGET = 'org/pokesplash/gts/config/options/CurrencyMessages.class'


def visual_strings(data):
    count = struct.unpack_from('>H', data, 8)[0]
    output = bytearray(data[:10])
    offset, index, changed = 10, 1, 0
    while index < count:
        tag = data[offset]
        offset += 1
        output.append(tag)
        if tag == 1:
            length = struct.unpack_from('>H', data, offset)[0]
            offset += 2
            value = data[offset:offset + length]
            offset += length
            replacement = LABELS.get(value, value)
            changed += replacement != value
            output.extend(struct.pack('>H', len(replacement)))
            output.extend(replacement)
        else:
            size = {3: 4, 4: 4, 5: 8, 6: 8, 7: 2, 8: 2, 9: 4, 10: 4,
                    11: 4, 12: 4, 15: 3, 16: 2, 17: 4, 18: 4, 19: 2, 20: 2}[tag]
            output.extend(data[offset:offset + size])
            offset += size
            if tag in (5, 6):
                index += 1
        index += 1
    output.extend(data[offset:])
    if changed != 2:
        raise ValueError(f'Expected exactly two visual labels; found {changed}')
    return bytes(output)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('source', type=Path)
    parser.add_argument('target', type=Path)
    args = parser.parse_args()
    if args.source.resolve() == args.target.resolve():
        raise ValueError('Source and target must be different files')
    with zipfile.ZipFile(args.source) as source, zipfile.ZipFile(args.target, 'w', zipfile.ZIP_DEFLATED) as target:
        for entry in source.infolist():
            data = source.read(entry.filename)
            if entry.filename == TARGET:
                data = visual_strings(data)
            elif entry.filename == 'fabric.mod.json':
                metadata = json.loads(data)
                assert metadata['name'] == 'Shirokai GTS'
                metadata['name'] = 'Zorykai GTS'
                data = (json.dumps(metadata, ensure_ascii=False, indent=2) + '\n').encode()
            target.writestr(entry, data)
    with zipfile.ZipFile(args.source) as source, zipfile.ZipFile(args.target) as target:
        assert source.namelist() == target.namelist()
        assert {n for n in source.namelist() if source.read(n) != target.read(n)} == {TARGET, 'fabric.mod.json'}
        assert target.testzip() is None
    print(args.target)


if __name__ == '__main__':
    main()

package org.pokesplash.gts.economy;

public final class SaleCoordinator {
   private final TransactionJournal journal;

   public SaleCoordinator(TransactionJournal journal) {
      this.journal = journal;
   }

   public synchronized boolean execute(TransactionJournal.Record record, SaleCoordinator.Port port) throws Exception {
      if (this.journal.blocked(record.listingId)) {
         throw new IllegalStateException("UNAVAILABLE");
      } else {
         port.validate();
         this.journal.phase(record, "PREPARED");
         this.journal.phase(record, "REMOVE_INTENT");
         port.removeListing();
         this.journal.phase(record, "DEBIT_INTENT");
         if (!port.debit()) {
            port.restoreListing();
            this.journal.phase(record, "ABORTED");
            return false;
         } else {
            this.journal.phase(record, "DEBITED");
            this.journal.phase(record, "CREDIT_INTENT");
            if (!port.credit()) {
               this.journal.phase(record, "REFUND_INTENT");
               if (!port.refund()) {
                  throw new IllegalStateException("REFUND_UNCERTAIN");
               } else {
                  this.journal.phase(record, "REFUNDED");
                  port.restoreListing();
                  this.journal.phase(record, "ABORTED");
                  return false;
               }
            } else {
               this.journal.phase(record, "CREDITED");
               this.journal.phase(record, "DELIVERY_INTENT");
               port.deliver();
               this.journal.phase(record, "DELIVERED");
               port.history();
               this.journal.phase(record, "SOLD");
               return true;
            }
         }
      }
   }

   public interface Port {
      void validate() throws Exception;

      void removeListing() throws Exception;

      void restoreListing() throws Exception;

      boolean debit() throws Exception;

      boolean credit() throws Exception;

      boolean refund() throws Exception;

      void deliver() throws Exception;

      void history() throws Exception;
   }
}

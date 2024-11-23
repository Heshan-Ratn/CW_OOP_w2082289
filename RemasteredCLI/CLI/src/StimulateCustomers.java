public class StimulateCustomers {
    private TicketPool ticketpool;

    public StimulateCustomers(TicketPool ticketpool) {
        this.ticketpool = ticketpool;
    }

    public void startBotPurcahse(){
        Customer customer1 = new Customer("robe234","rg34jh24");
        customer1.setCustomerRetrievalRate();
        customer1.setTicketPool(ticketpool);

        Customer customer2 = new Customer("bele234","rg34jh24");
        customer2.setCustomerRetrievalRate();
        customer2.setTicketPool(ticketpool);

        Customer customer3 = new Customer("relo234","rg34jh24");
        customer3.setCustomerRetrievalRate();
        customer3.setTicketPool(ticketpool);

        PurchaseRequest purchase1 = new PurchaseRequest("rolo",50);
        PurchaseRequest purchase2 = new PurchaseRequest("polo",50);
        PurchaseRequest purchase3 = new PurchaseRequest("yolo",50);
        PurchaseRequest purchase4 = new PurchaseRequest("holo",50);

        customer1.setPurchaseRequest(purchase1);
        Thread customerThread1 = new Thread(customer1);
        customerThread1.start();

        customer2.setPurchaseRequest(purchase2);
        Thread customerThread2 = new Thread(customer2);
        customerThread2.start();

        customer3.setPurchaseRequest(purchase3);
        Thread customerThread3 = new Thread(customer3);
        customerThread3.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        customer1.setPurchaseRequest(purchase4);
        Thread customerThread4 = new Thread(customer1);
        customerThread4.start();
    }
}

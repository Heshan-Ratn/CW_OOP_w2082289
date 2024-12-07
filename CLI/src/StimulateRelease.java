import java.util.List;

public class StimulateRelease{

    private TicketPool ticketpool;
    private int releaseRate;

    public StimulateRelease(TicketPool ticketpool, int releaseRate) {
        this.ticketpool = ticketpool;
        this.releaseRate = releaseRate;
    }

    public void startBotRelease(){
        Vendor vendor1 = Vendor.createVendor("gren234","20he40gn");
        vendor1.setTicketReleaseRate(releaseRate);
        vendor1.setTicketPool(ticketpool);
        Vendor vendor2 = Vendor.createVendor("kren234","20he40gn");
        vendor2.setTicketReleaseRate(releaseRate);
        vendor2.setTicketPool(ticketpool);
        Vendor vendor3 = Vendor.createVendor("jren234","20he40gn");
        vendor3.setTicketReleaseRate(releaseRate);
        vendor3.setTicketPool(ticketpool);

        List<Ticket> tickets1 = Ticket.createTicketsForVendor(vendor1.getVendorId(),100,"rolo",
                75.3,"2 hours","2022-10-20");
        List<Ticket> tickets2 = Ticket.createTicketsForVendor(vendor2.getVendorId(),100,"polo",
                75.3,"2 hours","2022-10-20");
        List<Ticket> tickets3 = Ticket.createTicketsForVendor(vendor3.getVendorId(),100,"yolo",
                75.3,"2 hours","2022-10-20");
        List<Ticket> tickets4 = Ticket.createTicketsForVendor(vendor1.getVendorId(),100,"holo",
                75.3,"2 hours","2022-10-20");

        vendor1.setTicketBatch(tickets1);
        Thread vendorThread1 = new Thread(vendor1);
        vendorThread1.start();

        vendor2.setTicketBatch(tickets2);
        Thread vendorThread2 = new Thread(vendor2);
        vendorThread2.start();

        vendor3.setTicketBatch(tickets3);
        Thread vendorThread3 = new Thread(vendor3);
        vendorThread3.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        vendor1.setTicketBatch(tickets4);
        Thread vendorThread4 = new Thread(vendor1);
        vendorThread4.start();
    }
}



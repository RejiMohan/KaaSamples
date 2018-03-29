public class TestEventEmitter {

    private static KaaClient kaaClient;

    private static ScheduledExecutorService scheduledExecutorService;

    public static void main(final String[] args) {

        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        // Create the Kaa desktop context for the application.
        final DesktopKaaPlatformContext desktopKaaPlatformContext = new DesktopKaaPlatformContext();

        kaaClient = Kaa.newClient(desktopKaaPlatformContext, new FirstKaaClientStateListener(), true);

        kaaClient.start();
        attachUserandEmitEvents();

        System.out.println("--= Press any key to exit =--");
        try {
            System.in.read();
        } catch (final IOException e) {
            System.out.println("IOException has occurred: {}" + e.getMessage());
        }
        System.out.println("Stopping Event Emitter...");
        scheduledExecutorService.shutdown();
        kaaClient.stop();
    }

    private static void attachUserandEmitEvents() {
        // attach user
        kaaClient.attachUser("testverifier", "82884822542100000000", new UserAttachCallback() {
            @Override
            public void onAttachResult(final UserAttachResponse response) {
                final EventFamilyFactory eventFamilyFactory = kaaClient.getEventFamilyFactory();

                final MyCustomECF myEcf = eventFamilyFactory.getMyCustomECF();

                for (int i = 0; i < 5; i++) {
                    MyCustomEvent event = new MyCustomEvent();
                    event.setCustId("ID00" + i);
                    event.setName("Name - " + i);

                    myEcf.sendEventToAll(event);
                }               
            }
        });
    }

    private static class FirstKaaClientStateListener extends SimpleKaaClientStateListener {

        @Override
        public void onStarted() {
            super.onStarted();
            System.out.println("Kaa client (Event Emitter) started");
        }

        @Override
        public void onStopped() {
            super.onStopped();
            System.out.println("Kaa client (Event Emitter) stopped");
        }
    }
}

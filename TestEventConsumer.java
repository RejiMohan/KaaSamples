public class TestEventConsumer {

    private static KaaClient kaaClient;

    private static ScheduledExecutorService scheduledExecutorService;

    public static void main(final String[] args) {
        System.out.println(SampleEventListener.class.getSimpleName() + " Event Consumer Starting!");

        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        // Create the Kaa desktop context for the application.
        final DesktopKaaPlatformContext desktopKaaPlatformContext = new DesktopKaaPlatformContext();
        kaaClient = Kaa.newClient(desktopKaaPlatformContext, new FirstKaaClientStateListener(), true);

        kaaClient.start();

        consumeEvents();

        System.out.println("--= Press any key to exit =--");
        try {
            System.in.read();
        } catch (final IOException e) {
            System.out.println("IOException has occurred: {}" + e.getMessage());
        }
        System.out.println("Stopping Event Consumer...");
        scheduledExecutorService.shutdown();
        kaaClient.stop();
    }

    private static class FirstKaaClientStateListener extends SimpleKaaClientStateListener {

        @Override
        public void onStarted() {
            super.onStarted();
            System.out.println("Kaa client (Event Consumer) started");
        }

        @Override
        public void onStopped() {
            super.onStopped();
            System.out.println("Kaa client (Event Consumer) stopped");
        }
    }

    public static void consumeEvents() {
        kaaClient.attachUser("testverifier", "82884822542100000000", new UserAttachCallback() {
            @Override
            public void onAttachResult(final UserAttachResponse response) {
                System.out.println("Attach response" + response.getResult());
            }
        });


        final EventFamilyFactory eventFamilyFactory = kaaClient.getEventFamilyFactory();
        final MyCustomECF myEcf = eventFamilyFactory.getMyCustomECF();

        myEcf.addListener(new MyCustomECF.Listener() {
            @Override
            public void onEvent(final MyCustomEvent event, final String source) {

                System.out.println("MyCustomEvent received from source : " + source + "Event Data Name : " + event.getName());

            }
        });
    }
}

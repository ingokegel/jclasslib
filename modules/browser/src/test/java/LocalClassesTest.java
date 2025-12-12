
public class LocalClassesTest {

    public static void test1() {
        new Runnable() {
            @Override
            public void run() {
                sleep();
            }
        }.run();
        class Test1 {
            public void run() {
                sleep();
            }
        }
        new Test1().run();
    }

    public void test2() {
        new Runnable() {
            @Override
            public void run() {
                sleep();
            }
        }.run();
        class Test2 {
            public void run() {
                sleep();
            }
        }
        new Test2().run();
        class Test1 {
            public void run() {
                new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                    }
                }.run();
            }
        }
        new Test1().run();
    }

    public void test3() {
        new Runnable() {
            @Override
            public void run() {
                new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                    }
                }.run();
                class Test2 {
                    public void run() {
                        sleep();
                    }
                }
                new Test2().run();
                class Test1 {
                    public void run() {
                        new Runnable() {
                            @Override
                            public void run() {
                                sleep();
                            }
                        }.run();
                    }
                }
                new Test1().run();
            }
        }.run();
    }

    public static class Inner {
        public static void test1() {
            new Runnable() {
                @Override
                public void run() {
                    sleep();
                }
            }.run();
            class Test1 {
                public void run() {
                    sleep();
                }
            }
            new Test1().run();
        }

        public void test2() {
            new Runnable() {
                @Override
                public void run() {
                    sleep();
                }
            }.run();
            class Test2 {
                public void run() {
                    sleep();
                }
            }
            new Test2().run();
            class Test1 {
                public void run() {
                    new Runnable() {
                        @Override
                        public void run() {
                            sleep();
                        }
                    }.run();
                }
            }
            new Test1().run();
        }
    }


    public static void main(String[] args) {
        LocalClassesTest.test1();
        new LocalClassesTest().test2();
        new LocalClassesTest().test3();
        Inner.test1();
        new LocalClassesTest.Inner().test2();
    }

    public static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

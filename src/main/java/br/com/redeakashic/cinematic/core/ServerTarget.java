package br.com.redeakashic.cinematic.core;

/** Owner-specified destination. No DNS, network, URL parsing or side effects here. */
public final class ServerTarget {
    public static final String NAME = "The End of Forbidden";
    public static final String HOST = "br2.xmxcloud.net";
    public static final int PORT = 10511;
    private ServerTarget() { }
    public static String address() { return HOST + ":" + PORT; }
}

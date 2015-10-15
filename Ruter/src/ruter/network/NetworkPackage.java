package ruter.network;

public class NetworkPackage {

	public int origin, destination;
	public long clock;
	public Object payload;

	public NetworkPackage(int origin, int destination, long clock, Object payload) {
		this.origin = origin;
		this.destination = destination;
		this.clock = clock;
		this.payload = payload;
	}

}

import java.io.EOFException;
import java.util.Random;
import java.util.Stack;

import com.sun.org.apache.bcel.internal.classfile.StackMap;
import com.sun.org.apache.xerces.internal.impl.dtd.models.DFAContentModel;

class Neighbor {
	public int vertexNum;
	public Neighbor next;

	public Neighbor(int vnm, Neighbor nbr) {
		this.vertexNum = vnm;
		next = nbr;
	}
}

class Vertex {
	public int name;
	public Neighbor adjList;

	public Vertex(int name, Neighbor nbrs) {
		this.name = name;
		this.adjList = nbrs;
	}

	public boolean contains(int v) {
		boolean result = false;
		Neighbor iterator = null;
		try {
			iterator = adjList;
			do {
				if (iterator.vertexNum == v)
					result = true;
			} while (iterator != null);
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
		return result;
	}
}

public class Graph {

	public Vertex[] adjLists;
	public boolean covered = false;
	public Stack<Neighbor> mainStack = new Stack<>();
	public int t = 1;
	public boolean q = false;

	public Stack<Neighbor> requestStack() {
		Stack<Neighbor> requested = new Stack<>();
		if (!mainStack.empty()) {
			int size = mainStack.size();
			for (int i = 0; i < size / (t + 1); i++)
				requested.push(mainStack.pop());

		}
		return requested;
	}

	public Graph(int n, int t, boolean q) {
		this.t = t;
		this.q = q;
		int i;
		int v1;
		int v2;
		adjLists = new Vertex[n];

		for (i = 0; i < adjLists.length; i++) {
			adjLists[i] = new Vertex(i, null);
		}

		Random rnd1 = new Random();
		int ribs = n + rnd1.nextInt(n);

		i = 0;
		while (i < ribs) {
			v1 = rnd1.nextInt(n);
			v2 = rnd1.nextInt(n);
			while (v2 == v1)
				v2 = rnd1.nextInt(n);
			adjLists[v1].adjList = new Neighbor(v2, adjLists[v1].adjList);
			if (ribs % 2 == 0)
				adjLists[v2].adjList = new Neighbor(v1, adjLists[v2].adjList);
			i++;
		}
	}

	public synchronized void dfs(int v, boolean[] visited) {
		synchronized (visited) {
			visited[v] = true;
		}
		System.out.println("visiting " + adjLists[v].name);
		for (Neighbor nbr = adjLists[v].adjList; nbr != null; nbr = nbr.next) {
			if (!visited[nbr.vertexNum]) {
				mainStack.add(nbr);
				System.out.println("\n" + adjLists[v].name + "--"
						+ adjLists[nbr.vertexNum].name);
				dfs(nbr.vertexNum, visited);
			}
		}
	}

	public void dfs() {
		boolean[] visited = new boolean[adjLists.length];

		for (int v = 0; v < visited.length; v++) {
			if (!visited[v]) {
				System.out.println("\nSTARTING AT " + adjLists[v].name);
				if (adjLists[v].adjList != null)
					mainStack.add(adjLists[v].adjList);
				dfs(v, visited);
			}
		}
		covered = true;
	}

	private int printGraph() {
		int i;
		for (i = 0; i < adjLists.length; i++) {
			Neighbor iterator = adjLists[i].adjList;
			System.out.print(adjLists[i].name);
			while (iterator != null) {
				System.out.print("-->" + iterator.vertexNum);
				iterator = iterator.next;
			}
			System.out.println();
		}
		return i;
	}

	public static void main(String[] args) throws IndexOutOfBoundsException,
			NullPointerException, InterruptedException {

		int i = 0;
		int n = 10240, t = 2;
		boolean q = false;
		System.out.println("start!");
		try {
			while (args.length > i) {
				System.out.println(args.length);
				if (args[i].equals("-n")) {
					n = Integer.parseInt(args[++i]);
					i++;
					// continue;
				} else if (args[i].equals("-t")) {
					t = Integer.parseInt(args[++i]);
					i++;
					// continue;
				} else if (args[i].equals("-q")) {
					q = true;
					i++;
					// continue;
				}

			}

			Thread threads[] = new Thread[t];
			Graph g = new Graph(n, t, q);
			g.printGraph();
			long startTime = System.currentTimeMillis();
			boolean[] visited = new boolean[n];

			for (i = 1; i < t; i++) {
				dfsRunnable d = new dfsRunnable(g, visited, g.mainStack);
				Thread thread = new Thread(d);
				thread.start();
				threads[i] = thread;
			}

			/*
			 * for(i = 0; i < t; i++) { threads[i].join(); }
			 */
			System.out.println(n);
			g.dfs();
			long endTime = System.currentTimeMillis();
			System.out.println("done! the execution last : "
					+ (double) (endTime - startTime) / 1000);

		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();

		}

	}

}

import java.io.EOFException;
import java.util.Calendar;
import java.util.EmptyStackException;
import java.util.Random;
import java.util.Stack;

import sun.applet.Main;

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

	public synchronized Stack<Neighbor> requestStack() {
		Stack<Neighbor> requested = new Stack<Neighbor>();
		int size = mainStack.size();
			try {			
				for (int i = 0; i < size / t; i++)
					if (!mainStack.empty())
						requested.push(mainStack.pop());
				if (!q)
					System.out.println("Stack has been requested here!");
			} catch (EmptyStackException e) {
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

	public void dfs(int v, boolean[] visited, Stack<Neighbor> specify) {
		synchronized (visited) {
			visited[v] = true;
		}
		
		if (q == false)
			System.out.println("visiting " + adjLists[v].name);
		for (Neighbor nbr = adjLists[v].adjList; nbr != null; nbr = nbr.next) {
			if (!visited[nbr.vertexNum]) {
				specify.add(nbr);
			}
		}
		//System.out.println("Mainstack: " + mainStack.size());
		synchronized (mainStack) {
			if(mainStack.size() > 30)
				mainStack.notifyAll();
		}
		while (!specify.empty()) {
			/*
			 * if (q == false) System.out.println("Main stack has : " +
			 * mainStack.size());
			 */
			try {
				if (!visited[specify.peek().vertexNum]) {
					if (q == false)
						System.out.println("\n" + adjLists[v].name + "--"
								+ adjLists[specify.peek().vertexNum].name);
					dfs(specify.pop().vertexNum, visited, specify);
				} else {
					specify.pop();
				}
			} catch (EmptyStackException e) {
			}

		}
	}

	public void dfs() {

		for (int j = 1; j <= t; j++) {
			covered = false;
			long startTime = System.currentTimeMillis();

			boolean[] visited = new boolean[adjLists.length];
			for (int i = 1; i < j; i++) {
				dfsRunnable d = new dfsRunnable(this, visited);
				Thread thread = new Thread(d);
				thread.start();
			}

			for (int v = 0; v < visited.length; v++) {
				if (!visited[v]) {
					if (q == false)
						System.out.println("\nSTARTING AT " + adjLists[v].name);
					/*
					 * if (adjLists[v].adjList != null)
					 * mainStack.add(adjLists[v].adjList);
					 */
					dfs(v, visited, this.mainStack);
				}
			}
			covered = true;
			long endTime = System.currentTimeMillis();
			System.out.printf("done! the execution with %d threads lasted: %d%n", j , (endTime - startTime));

		}
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
		int n = 10000, t = 8;
		boolean q = true;
		// System.out.println("start!");
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
			//g.printGraph();

			// boolean[] visited = new boolean[n];

			/*
			 * for (i = 0; i < t; i++) { dfsRunnable d = new dfsRunnable(g,
			 * visited, g.mainStack); Thread thread = new Thread(d);
			 * 
			 * thread.start(); threads[i] = thread; }
			 */

			/*
			 * for (i = 0; i < t; i++) { threads[i].join(); }
			 */

			g.dfs();

		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();

		}

	}
}

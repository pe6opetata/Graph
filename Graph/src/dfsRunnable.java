import java.util.EmptyStackException;
import java.util.Stack;

public class dfsRunnable implements Runnable {

	private Graph graph;
	private boolean[] visited;

	// private Stack<Neighbor> mainStack;

	public dfsRunnable(Graph g, boolean[] vis) {
		graph = g;
		visited = vis;
		// mainStack = st;
	}

	public void run() {

		Stack<Neighbor> workStack = new Stack<>();
		while (graph.covered == false) {
			if (workStack.empty()) {
				synchronized (graph.mainStack) {
					try {
						graph.mainStack.wait();
						workStack = graph.requestStack();
					} catch (InterruptedException e) {
					}

				}
			}

			// System.out.println("We don't have elements in our stack :(");

			 if (graph.q == false)
			//if (workStack.size() != 0)
				System.out.println("We have " + workStack.size()
						+ " element(s) in our stack!");
			try {
				if (!visited[workStack.peek().vertexNum]) {
					graph.dfs(workStack.pop().vertexNum, visited, workStack);
				} else
					workStack.pop();
			} catch (EmptyStackException e) {
				continue;
			}
			/*
			 * Neighbor current = workStack.pop();
			 * System.out.println("visiting " +
			 * graph.adjLists[current.vertexNum].name);
			 * visited[current.vertexNum] = true; for (Neighbor nbr =
			 * graph.adjLists[current.vertexNum].adjList; nbr != null; nbr =
			 * nbr.next) { if (!visited[nbr.vertexNum]) {
			 * System.out.println("\n" + graph.adjLists[current.vertexNum].name
			 * + "--" + graph.adjLists[nbr.vertexNum].name);
			 * graph.dfs(nbr.vertexNum, visited); } }
			 */
		}

	}

}

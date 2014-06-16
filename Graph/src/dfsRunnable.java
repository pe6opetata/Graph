import java.util.Stack;

public class dfsRunnable implements Runnable {

	private Graph graph;
	private boolean[] visited;
	private Stack<Neighbor> mainStack;

	public dfsRunnable(Graph g, boolean[] vis, Stack<Neighbor> st) {
		graph = g;
		visited = vis;
		mainStack = st;
	}

	public void run() {
		Stack<Neighbor> workStack = new Stack<>();
		while (graph.covered == false) {
			if (workStack.empty()) {
				workStack = graph.requestStack();
			} else {
				Neighbor current = workStack.pop();
				System.out.println("visiting "
						+ graph.adjLists[current.vertexNum].name);

				for (Neighbor nbr = graph.adjLists[current.vertexNum].adjList; nbr != null; nbr = nbr.next) {
					if (!visited[nbr.vertexNum]) {
						System.out.println("\n"
								+ graph.adjLists[current.vertexNum].name + "--"
								+ graph.adjLists[nbr.vertexNum].name);
						graph.dfs(nbr.vertexNum, visited);
					}
				}
			}

		}

	}

}

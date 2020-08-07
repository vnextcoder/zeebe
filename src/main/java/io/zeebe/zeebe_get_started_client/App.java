package io.zeebe.zeebe_get_started_client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.DeploymentEvent;
import io.zeebe.client.api.response.WorkflowInstanceEvent;
import io.zeebe.client.api.worker.JobWorker;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final ZeebeClient client = ZeebeClient.newClientBuilder()
				// change the contact point if needed
				.brokerContactPoint("127.0.0.1:26500").usePlaintext().build();

		System.out.println("Connected.");

		final DeploymentEvent deployment = client.newDeployCommand().addResourceFromClasspath("order-process.bpmn")
				.send().join();

		final int version = deployment.getWorkflows().get(0).getVersion();
		System.out.println("Workflow deployed. Version: " + version);


		final DeploymentEvent deployment2 = client.newDeployCommand().addResourceFromClasspath("cancel-process.bpmn")
				.send().join();

		final int version2 = deployment2.getWorkflows().get(0).getVersion();
		System.out.println("Cancellation Workflow deployed. Version: " + version2);

		

		

		final Map<String, Object> data = new HashMap<>();
		data.put("orderId", 31243);
		data.put("orderItems", Arrays.asList(435, 182, 376));

		final WorkflowInstanceEvent wfInstance = client.newCreateInstanceCommand().bpmnProcessId("Process_Order")
				.latestVersion().variables(data).send().join();

		final long workflowInstanceKey = wfInstance.getWorkflowInstanceKey();
		System.out.println("Workflow instance created. Key: " + workflowInstanceKey);
		// ...

		final JobWorker jobWorker = client.newWorker().jobType("payment-service").handler((jobClient, job) -> {
			final Map<String, Object> variables = job.getVariablesAsMap();

			System.out.println("Process order: " + variables.get("orderId"));
			double price = 46.50;
			System.out.println("Collect money: $" + price);

			// ...

			final Map<String, Object> result = new HashMap<>();
			result.put("totalPrice", price);

			jobClient.newCompleteCommand(job.getKey()).variables(result).send().join();
		}).fetchVariables("orderId").open();

		final JobWorker jobWorker2 = client.newWorker().jobType("fetch-service").handler((jobClient, job) -> {
			final Map<String, Object> variables = job.getVariablesAsMap();

			System.out.println("Process order: " + variables.get("orderId"));
			double price = 46.50;
			System.out.println("Collect money: $" + price);

			// ...

			final Map<String, Object> result = new HashMap<>();
			result.put("totalPrice", price);

			jobClient.newCompleteCommand(job.getKey()).variables(result).send().join();
		}).fetchVariables("orderId").open();

		final JobWorker jobWorker3 = client.newWorker().jobType("shipping-service").handler((jobClient, job) -> {
			final Map<String, Object> variables = job.getVariablesAsMap();

			System.out.println("Process order: " + variables.get("orderId"));
			String shippingReference = "AWB003303";
			System.out.println("Shipping Reference is : $" + shippingReference);

			// ...

			final Map<String, Object> result = new HashMap<>();
			result.put("shippingReference", shippingReference);

			jobClient.newCompleteCommand(job.getKey()).variables(result).send().join();
		}).fetchVariables("shippingReference").open();

		
		
		/*
		 * final WorkflowInstanceEvent wfCancellationInstance =
		 * client.newCreateInstanceCommand().bpmnProcessId("Process_Cancellation")
		 * .latestVersion().variables(data).send().join();
		 * 
		 * final long cancellationWorkflowInstanceKey =
		 * wfCancellationInstance.getWorkflowInstanceKey();
		 * System.out.println("Cancellation Workflow instance created. Key: " +
		 * cancellationWorkflowInstanceKey);
		 */		// ...

		
		
		final JobWorker jobWorker4 = client.newWorker().jobType("Cancel_Order").handler((jobClient, job) -> {
			final Map<String, Object> variables = job.getVariablesAsMap();

			System.out.println("Process order: " + variables.get("orderId"));
			double price = 46.50;
			System.out.println("Collect money: $" + price);

			// ...

			final Map<String, Object> result = new HashMap<>();
			result.put("totalPrice", price);

			jobClient.newCompleteCommand(job.getKey()).variables(result).send().join();
		}).fetchVariables("orderId").open();

		
		
		client.close();
		System.out.println("Closed.");

	}
}

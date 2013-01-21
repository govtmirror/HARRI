package gov.usgs.cida.harri.service;

import org.teleal.cling.UpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;

public class ExampleServiceCalls {
		public static void doExampleServiceCall(final UpnpService upnpService, final RemoteDevice device){
			ServiceId serviceId = new UDAServiceId("ExampleHarriService"); //NOTE: a service on the device is annotated with this value
			Service exampleHarriAction;
			if ((exampleHarriAction = device.findService(serviceId)) != null) {

				System.out.println("HARRI Service discovered on device " + device.getDetails().getModelDetails().getModelName() + ": " + exampleHarriAction);
				executeAction(upnpService, exampleHarriAction);
			}
		}
		
		private static void executeAction(UpnpService upnpService, Service exampleHarriActionService) {

			ActionInvocation setTargetInvocation =
					new ExampleHarriActionInvocation(exampleHarriActionService);

			// Executes asynchronous in the background
			upnpService.getControlPoint().execute(
					new ActionCallback(setTargetInvocation) {

						@Override
						public void success(ActionInvocation invocation) {
							assert invocation.getOutput().length == 0;
							System.out.println("Successfully called remote action on HARRI device!");
						}

						@Override
						public void failure(ActionInvocation invocation,
								UpnpResponse operation,
								String defaultMsg) {
							System.err.println(defaultMsg);
						}
					}
					);

		}

		private static class ExampleHarriActionInvocation extends ActionInvocation {
			ExampleHarriActionInvocation(Service service) {
				super(service.getAction("DoExampleAction")); //NOTE: this string is a method in the service
				try {
					// Throws InvalidValueException if the value is of wrong type
					setInput("HarriManagerId", "EXAMPLE_HARRI_MANAGER_ID"); //TODO get this example harri manager id from somewhere useful
				} catch (InvalidValueException ex) {
					System.err.println(ex.getMessage());
					System.exit(1);
				}
			}
		}
}

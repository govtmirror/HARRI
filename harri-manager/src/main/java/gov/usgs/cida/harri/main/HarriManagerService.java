package gov.usgs.cida.harri.main;

import gov.usgs.cida.harri.service.ExampleServiceCalls;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.*;
import org.teleal.cling.model.action.*;
import org.teleal.cling.model.message.*;
import org.teleal.cling.model.message.header.*;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.*;
import org.teleal.cling.registry.*;

public class HarriManagerService implements Runnable {
	public static final String DEVICE_PREFIX = "HARRI_Device";
	public static final String DEVICE_MANUFACTURER = "CIDA";

	public static void main(String[] args) throws Exception {
		// Start a user thread that runs the UPnP stack
		Thread clientThread = new Thread(new HarriManagerService());
		clientThread.setDaemon(false);
		clientThread.start();

	}

	public void run() {
		try {

			UpnpService upnpService = new UpnpServiceImpl();

			// Add a listener for device registration events
			upnpService.getRegistry().addListener(
					createRegistryListener(upnpService)
					);

			// Broadcast a search message for all devices
			upnpService.getControlPoint().search(
					new STAllHeader()
					);

		} catch (Exception ex) {
			System.err.println("Exception occured: " + ex);
			System.exit(1);
		}
	}

	private RegistryListener createRegistryListener(final UpnpService upnpService) {
		return new DefaultRegistryListener() {
			private boolean isHarriDevice(final RemoteDevice device) {
				return device.getDetails().getManufacturerDetails()!=null &&
						device.getDetails().getManufacturerDetails().getManufacturer().equals(DEVICE_MANUFACTURER) && 
						device.getDetails().getModelDetails().getModelName().contains(DEVICE_PREFIX);
			}
			
			@Override
			public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
				//if not a HARRI device, do nothing
				if(!isHarriDevice(device)){
					return;
				}
				
				//TODO do something more intelligent
				System.out.println("HARRI Device has been added: " + device.getDetails().getModelDetails().getModelName());
				ExampleServiceCalls.doExampleServiceCall(upnpService, device); //TODO delete when not needed
			}

			@Override
			public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
				if(!isHarriDevice(device)){
					return;
				}
				
				//TODO do something more intelligent
				System.out.println("HARRI Device " + device.getDetails().getModelDetails().getModelName() + " has been removed!");
			}

		};
	}
}

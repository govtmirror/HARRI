package gov.usgs.cida.harri.main;

import gov.usgs.cida.harri.service.ExampleServiceCalls;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.*;
import org.teleal.cling.model.meta.*;
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

        @Override
	public void run() {
		try {
                        System.out.println("HARRI Manager Service starting");
			UpnpService upnpService = new UpnpServiceImpl();

			// Add a listener for device registration events
			upnpService.getRegistry().addListener(
					createRegistryListener(upnpService)
					);

			System.out.println("Broadcasting a search message for all known devices");
			upnpService.getControlPoint().search(
					new STAllHeader()
					);
                        
                        System.out.println("HARRI Manager Service started successfully");

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
				System.out.println("HARRI Device " + device.getDetails().getModelDetails().getModelName() + " has been removed");
			}
                        
                    @Override
                        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
                            System.out.println("HARRI Device " + device.getDetails().getModelDetails().getModelName() + " has been updated");
                        }
                    
                    @Override
                    public void beforeShutdown(Registry registry) {
                        System.out.println("HARRI Device is shutting sown. Devices in registry: " + registry.getDevices().size());
                    }

                    @Override
                    public void afterShutdown() {
                        System.out.println("HARRI Registry has been shut down");

                    }



		};
	}
}

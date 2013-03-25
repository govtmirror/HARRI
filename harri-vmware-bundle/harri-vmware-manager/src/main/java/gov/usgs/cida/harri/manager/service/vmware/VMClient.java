package gov.usgs.cida.harri.manager.service.vmware;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RetrieveOptions;
import com.vmware.vim25.RetrieveResult;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO this whole thing uses static members to mimic simple client, clean up!
//TODO uncomment LOG statements, this was done for demo readability
public class VMClient {
	static Logger LOG = LoggerFactory.getLogger(VMClient.class);

    private static String url;
    private static String userName;
    private static String password;

    public VMClient(String url, String userName, String password) {}
    
    public static List<String> getVirtualMachines(String iurl, String iuserName, String ipassword) {
    	List<String> result = null;
        
    	url = iurl;
        userName = iuserName;
        password = ipassword;

        try {
            connect();
            result = getVirtualMachines();
        } catch (SOAPFaultException sfe) {
                 printSoapFaultException(sfe);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                disconnect();
                return result;
            } catch (SOAPFaultException sfe) {
                printSoapFaultException(sfe);
            } catch (Exception e) {
                LOG.info("Failed to disconnect - " + e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    private static class TrustAllTrustManager implements javax.net.ssl.TrustManager,
                                                        javax.net.ssl.X509TrustManager {

      static Logger LOG = LoggerFactory.getLogger(VMClient.class);
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
         return null;
      }

      public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
         return true;
      }

      public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
         return true;
      }

      public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                                     String authType)
         throws java.security.cert.CertificateException {
         return;
      }

      public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                                     String authType)
         throws java.security.cert.CertificateException {
         return;
      }
    }

    private static final ManagedObjectReference SVC_INST_REF = new ManagedObjectReference();
    private static ManagedObjectReference propCollectorRef;
    private static ManagedObjectReference rootRef;
    private static VimService vimService;
    private static VimPortType vimPort;
    private static ServiceContent serviceContent;
    private static final String SVC_INST_NAME = "ServiceInstance";

    private static boolean help = false;
    private static boolean isConnected = false;

    private static void trustAllHttpsCertificates()
      throws Exception {
      // Create a trust manager that does not validate certificate chains:
      javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
      javax.net.ssl.TrustManager tm = new VMClient.TrustAllTrustManager();
      trustAllCerts[0] = tm;
      javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
      javax.net.ssl.SSLSessionContext sslsc = sc.getServerSessionContext();
      sslsc.setSessionTimeout(0);
      sc.init(null, trustAllCerts, null);
      javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    /**
    * Establishes session with the virtual center server.
    *
    * @throws Exception the exception
    */
    private static void connect()
      throws Exception {

      HostnameVerifier hv = new HostnameVerifier() {
         public boolean verify(String urlHostName, SSLSession session) {
            return true;
         }
      };
      trustAllHttpsCertificates();
      HttpsURLConnection.setDefaultHostnameVerifier(hv);

      SVC_INST_REF.setType(SVC_INST_NAME);
      SVC_INST_REF.setValue(SVC_INST_NAME);

      vimService = new VimService();
      vimPort = vimService.getVimPort();
      Map<String, Object> ctxt =
         ((BindingProvider) vimPort).getRequestContext();

      ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
      ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

      serviceContent = vimPort.retrieveServiceContent(SVC_INST_REF);
      vimPort.login(serviceContent.getSessionManager(),
            userName,
            password, null);
      isConnected = true;

      propCollectorRef = serviceContent.getPropertyCollector();
      rootRef = serviceContent.getRootFolder();
    }

    /**
    * Disconnects the user session.
    *
    * @throws Exception
    */
    private static void disconnect()
      throws Exception {
      if (isConnected) {
         vimPort.logout(serviceContent.getSessionManager());
      }
      isConnected = false;
    }

    /**
    * Uses the new RetrievePropertiesEx method to emulate the now deprecated RetrieveProperties method
    *
    * @param listpfs
    * @return list of object content
    * @throws Exception
    */
    private static List<ObjectContent> retrievePropertiesAllObjects(List<PropertyFilterSpec> listpfs)
      throws Exception {
    	Logger LOG = LoggerFactory.getLogger(VMClient.class);

      RetrieveOptions propObjectRetrieveOpts = new RetrieveOptions();

      List<ObjectContent> listobjcontent = new ArrayList<ObjectContent>();

      try {
         RetrieveResult rslts =
            vimPort.retrievePropertiesEx(propCollectorRef,
                                         listpfs,
                                         propObjectRetrieveOpts);
         if (rslts != null && rslts.getObjects() != null &&
               !rslts.getObjects().isEmpty()) {
            listobjcontent.addAll(rslts.getObjects());
         }
         String token = null;
         if(rslts != null && rslts.getToken() != null) {
            token = rslts.getToken();
         }
         while (token != null && !token.isEmpty()) {
            rslts = vimPort.continueRetrievePropertiesEx(propCollectorRef, token);
            token = null;
            if (rslts != null) {
               token = rslts.getToken();
               if (rslts.getObjects() != null && !rslts.getObjects().isEmpty()) {
                 listobjcontent.addAll(rslts.getObjects());
               }
            }
         }
      } catch (SOAPFaultException sfe) {
         printSoapFaultException(sfe);
      } catch (Exception e) {
         LOG.info(" : Failed Getting Contents");
         e.printStackTrace();
      }

      return listobjcontent;
    }

    private static List<String> getVirtualMachines() throws Exception {
        List<String> result = new ArrayList<String>();
    
        TraversalSpec resourcePoolTraversalSpec = new TraversalSpec();
        resourcePoolTraversalSpec.setName("resourcePoolTraversalSpec");
        resourcePoolTraversalSpec.setType("ResourcePool");
        resourcePoolTraversalSpec.setPath("resourcePool");
        resourcePoolTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec rpts = new SelectionSpec();
        rpts.setName("resourcePoolTraversalSpec");
        resourcePoolTraversalSpec.getSelectSet().add(rpts);

        TraversalSpec computeResourceRpTraversalSpec = new TraversalSpec();
        computeResourceRpTraversalSpec.setName("computeResourceRpTraversalSpec");
        computeResourceRpTraversalSpec.setType("ComputeResource");
        computeResourceRpTraversalSpec.setPath("resourcePool");
        computeResourceRpTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec rptss = new SelectionSpec();
        rptss.setName("resourcePoolTraversalSpec");
        computeResourceRpTraversalSpec.getSelectSet().add(rptss);

        TraversalSpec computeResourceHostTraversalSpec = new TraversalSpec();
        computeResourceHostTraversalSpec.setName("computeResourceHostTraversalSpec");
        computeResourceHostTraversalSpec.setType("ComputeResource");
        computeResourceHostTraversalSpec.setPath("host");
        computeResourceHostTraversalSpec.setSkip(Boolean.FALSE);

        TraversalSpec datacenterHostTraversalSpec = new TraversalSpec();
        datacenterHostTraversalSpec.setName("datacenterHostTraversalSpec");
        datacenterHostTraversalSpec.setType("Datacenter");
        datacenterHostTraversalSpec.setPath("hostFolder");
        datacenterHostTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec ftspec = new SelectionSpec();
        ftspec.setName("folderTraversalSpec");
        datacenterHostTraversalSpec.getSelectSet().add(ftspec);

        TraversalSpec datacenterVmTraversalSpec = new TraversalSpec();
        datacenterVmTraversalSpec.setName("datacenterVmTraversalSpec");
        datacenterVmTraversalSpec.setType("Datacenter");
        datacenterVmTraversalSpec.setPath("vmFolder");
        datacenterVmTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec ftspecs = new SelectionSpec();
        ftspecs.setName("folderTraversalSpec");
        datacenterVmTraversalSpec.getSelectSet().add(ftspecs);

        TraversalSpec folderTraversalSpec = new TraversalSpec();
        folderTraversalSpec.setName("folderTraversalSpec");
        folderTraversalSpec.setType("Folder");
        folderTraversalSpec.setPath("childEntity");
        folderTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec ftrspec = new SelectionSpec();
        ftrspec.setName("folderTraversalSpec");
        List<SelectionSpec> ssarray = new ArrayList<SelectionSpec>();
        ssarray.add(ftrspec);
        ssarray.add(datacenterHostTraversalSpec);
        ssarray.add(datacenterVmTraversalSpec);
        ssarray.add(computeResourceRpTraversalSpec);
        ssarray.add(computeResourceHostTraversalSpec);
        ssarray.add(resourcePoolTraversalSpec);

        folderTraversalSpec.getSelectSet().addAll(ssarray);
        PropertySpec props = new PropertySpec();
        props.setAll(Boolean.FALSE);
        props.getPathSet().add("name");
        props.setType("ManagedEntity");
        List<PropertySpec> propspecary = new ArrayList<PropertySpec>();
        propspecary.add(props);

        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.getPropSet().addAll(propspecary);

        spec.getObjectSet().add(new ObjectSpec());
        spec.getObjectSet().get(0).setObj(rootRef);
        spec.getObjectSet().get(0).setSkip(Boolean.FALSE);
        spec.getObjectSet().get(0).getSelectSet().add(folderTraversalSpec);

        List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
        listpfs.add(spec);
        List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);

        // If we get contents back. print them out.
        if (listobjcont != null) {
         ObjectContent oc = null;
         ManagedObjectReference mor = null;
         DynamicProperty pc = null;
         for (int oci = 0; oci < listobjcont.size(); oci++) {
            oc = listobjcont.get(oci);
            mor = oc.getObj();

            List<DynamicProperty> listdp = oc.getPropSet();
//            LOG.info("Object Type : " + mor.getType());
//            LOG.info("Reference Value : " + mor.getValue());
            
            if (mor.getType().equals("VirtualMachine")) {
                result.add(mor.getValue());
            }

            if (listdp != null) {
               for (int pci = 0; pci < listdp.size(); pci++) {
                  pc = listdp.get(pci);
//                  LOG.info("   Property Name : " + pc.getName());
                  if ((pc != null)) {
                     if (!pc.getVal().getClass().isArray()) {
//                       LOG.info("   Property Value : " + pc.getVal());
                       if(mor.getType().equals("VirtualMachine")) {
                    	   result.add(pc.getVal().toString());
                       }
                     } else {
                        List<Object> ipcary = new ArrayList<Object>();
                        ipcary.add(pc.getVal());
//                        LOG.info("Val : " + pc.getVal());
                        for (int ii = 0; ii < ipcary.size(); ii++) {
                           Object oval = ipcary.get(ii);
                           if (oval.getClass().getName().indexOf(
                                 "ManagedObjectReference") >= 0) {
                              ManagedObjectReference imor = (ManagedObjectReference) oval;

//                              LOG.info("Inner Object Type : "
//                                    + imor.getType());
//                              LOG.info("Inner Reference Value : "
//                                    + imor.getValue());
                              
                              if (imor.getType().equals("VirtualMachine")) {
                                  result.add(imor.getValue());
                              }
                           } else {
//                              LOG.info("Inner Property Value : " + oval);
                           }
                        }
                     }
                  }
               }
            }
         }
        } else {
//         LOG.info("No Managed Entities retrieved!");
        }

        return result;
    }

    private static void printSoapFaultException(SOAPFaultException sfe) {
    	Logger LOG = LoggerFactory.getLogger(VMClient.class);
      LOG.info("SOAP Fault -");
      if (sfe.getFault().hasDetail()) {
         LOG.info(sfe.getFault().getDetail().getFirstChild().getLocalName());
      }
      if (sfe.getFault().getFaultString() != null) {
         LOG.info("\n Message: " + sfe.getFault().getFaultString());
      }
    }

}

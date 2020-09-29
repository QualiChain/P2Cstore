package com.thesis.inesc.CloudStorage;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.thesis.inesc.Exceptions.NoSuchFileFoundException;
import com.thesis.inesc.Utilities.FilesUtilities;
import com.thesis.inesc.Utilities.HashFunction;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStoreContext;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.s3.S3Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.StrictMath.toIntExact;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Class that handles the cloud connectivity and associated operations
 *
 * @author Marcelo Regra da Silva
 * @created 07/05/2020
 */
public class CloudConnectivity {
    private String credentialsAWSS3;
    private String identityAWSS3;
    private String jsonFileGCP;
    private final String AWSS3_PROVIDER = "aws-s3";
    private final String GCP_PROVIDER = "google-cloud-storage";
    private final String CONTAINER_NAME = "marcelosilva-jclouds-container1";
    private final String CONFIG_FILE_PATH = "./Resources/config.properties";

    public CloudConnectivity(){
        super();
    }

    /**
     * @param ownerID       The ownerID
     * @param filePath      The file path to add to the cloud
     */
    public void addDataToAWSS3Cloud(String ownerID, String filePath) throws NoSuchFileFoundException {
        getCredentialsAWSS3();
        CloudBlobContext creaetCloudBlobContext = createCloudBlobContextAWSS3(true);
        String fileData = getFileData(filePath);
        ByteSource payload = ByteSource.wrap(fileData.getBytes(Charsets.UTF_8));
        try {
            String fileHash = HashFunction.fromByteToBase58(FilesUtilities.getFileHash(fileData.getBytes()));
            String blobName = ownerID + "/" + fileHash;
            addBlob(creaetCloudBlobContext.getBlobStore(), blobName, payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        creaetCloudBlobContext.getContext().close();
    }

    /**
     * @param ownerID       The ownerID
     * @param filePath      The file path to add to the cloud
     */
    public void addDataToGoogleCloud(String ownerID, String filePath) throws NoSuchFileFoundException {
        getCredentialsGCP();
        CloudBlobContext cloudBlobContext = createCloudBlobContextGCP(getCredentialFromJsonKeyFile(this.jsonFileGCP), true);
        try {
            // Create Container
            String fileData = getFileData(filePath);
            byte[] res = fileData.getBytes(Charsets.UTF_8);
            System.out.println(res.length);
            ByteSource payload = ByteSource.wrap(res);
            /*Uncomment  for debugging purposes
            try {
                System.out.println((payload.size()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }*/
            String fileHash = HashFunction.fromByteToBase58(FilesUtilities.getFileHash(fileData.getBytes()));
            String blobName = ownerID+ "/" + fileHash;
            addBlob(cloudBlobContext.getBlobStore(), blobName, payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cloudBlobContext.getContext().close();
    }

    /**
     * @param fileHash          The file hash which is the file name
     * @param ownerID           The file owner ID
     * @return String           File contents
     */
    public String getDataFromAWSS3Cloud(String fileHash, String ownerID) throws NoSuchFileFoundException {
        getCredentialsAWSS3();
        CloudBlobContext cloudBlobContext = createCloudBlobContextAWSS3(false);
        Blob blob = getBlobFromCloud(cloudBlobContext, ownerID, fileHash);
        S3Client api = cloudBlobContext.getContext().unwrapApi(S3Client.class);
        int size = 0;
        try {
            size = toIntExact(api.headObject(CONTAINER_NAME, ownerID+"/"+fileHash).getContentMetadata().getContentLength());
        } catch(NullPointerException e1){
            System.out.print("Data not in AWS S3.");
            System.out.flush();
            return null;
        }
        return getFileFromCloudOperation(blob, size, cloudBlobContext);
    }

    /**
     * @param fileHash          The file hash which is the file name
     * @param ownerID           The file owner ID
     * @return String           File contents
     */
    public String getDataFromGoogleCloud(String ownerID, String fileHash) throws NoSuchFileFoundException {
        getCredentialsGCP();
        CloudBlobContext cloudBlobContext = createCloudBlobContextGCP(getCredentialFromJsonKeyFile(this.jsonFileGCP), false);
        Blob blob = getBlobFromCloud(cloudBlobContext, ownerID, fileHash);
        int size = 0;
        try {
            size = toIntExact(blob.getMetadata().getSize());
        } catch(NullPointerException e1){
            System.out.println("Data not in GCP.");
            return null;
        }
        return getFileFromCloudOperation(blob, size, cloudBlobContext);
    }

    /**
     * @param blob                  The cloud blob
     * @param size                  A integer that idicates the size of the metada of the blob
     * @param cloudBlobContext      Cloud blob context
     * @return String               File contents
     */
    private String getFileFromCloudOperation(Blob blob, int size, CloudBlobContext cloudBlobContext){
        InputStream is = null;
        try {
            is = blob.getPayload().openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[size];
        try {
            is.read(bytes);
            System.out.println(new String(bytes));
            is.close();
            cloudBlobContext.getContext().close();
            System.out.print("bss-termianl $ ");
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cloudBlobContext.getContext().close();
        return null;
    }

    /**
     * @param cloudBlobContext      Cloud blob context
     * @param container             The ownerID
     * @param fileName              The fileName = fileHash
     * @return Blob                 
     */
    private Blob getBlobFromCloud(CloudBlobContext cloudBlobContext, String container, String fileName){
        Blob blob = null;
        try {
            blob = cloudBlobContext.getBlobStore().getBlob(CONTAINER_NAME, container + "/" + fileName);
        } catch(org.jclouds.rest.AuthorizationException e){
            System.out.println("No authorization");
            return null;
        }
        return blob;
    }

    /**
     * @param fileOwner             The file owner ID
     * @param fileHash              The file hash which is the file name
     */
    public void deleteFromGoogleCloud(String fileOwner, String fileHash) throws NoSuchFileFoundException {
        getCredentialsGCP();
        CloudBlobContext cloudBlobContext = createCloudBlobContextGCP(getCredentialFromJsonKeyFile(this.jsonFileGCP), false);
        deleteFileFromCloudOperation(cloudBlobContext, fileHash, fileOwner);
    }

    /**
     * @param fileHash              The file hash which is the file name
     * @param fileOwner             The file owner ID
     */
    public void deleteDataFromAWSS3Cloud(String fileHash, String fileOwner) throws NoSuchFileFoundException {
        getCredentialsAWSS3();
        //System.out.println("Connecting to AWS S3...");
        CloudBlobContext cloudBlobContext = createCloudBlobContextAWSS3(false);
        deleteFileFromCloudOperation(cloudBlobContext, fileHash, fileOwner);
    }

    /**
     * @param cloudBlobContext      Cloud blob context
     * @param fileHash              The file hash which is the file name
     * @param fileOwner             The file owner ID
     */
    private void deleteFileFromCloudOperation(CloudBlobContext cloudBlobContext, String fileHash, String fileOwner){
        cloudBlobContext.getBlobStore().removeBlob(CONTAINER_NAME, fileOwner+"/"+fileHash);
        cloudBlobContext.getContext().close();
    }

    /**
     * @param ownerID               The file owner ID
     * @param fileHash              The file hash which is the file name
     * @param newFilePath           The new file path to add to the cloud
     */
    public void updateDataOnAWSS3Cloud(String ownerID, String fileHash, String newFilePath) throws NoSuchFileFoundException {
        getCredentialsAWSS3();
        CloudBlobContext cloudBlobContext = createCloudBlobContextAWSS3(false);
        updateFileFromCloudOperation(cloudBlobContext, ownerID, fileHash, newFilePath);
    }

    /**
     * @param ownerID               The file owner ID
     * @param fileHash              The file hash which is the file name
     * @param newFilePath           The new file path to add to the cloud
     */
    public void updateDataOnGoogleCloud(String ownerID, String fileHash, String newFilePath) throws NoSuchFileFoundException {
        getCredentialsGCP();
        CloudBlobContext cloudBlobContext = createCloudBlobContextGCP(getCredentialFromJsonKeyFile(this.jsonFileGCP), false);
        updateFileFromCloudOperation(cloudBlobContext, ownerID, fileHash, newFilePath);
    }

    /**
     * @param cloudBlobContext      Cloud blob context
     * @param ownerID               The file owner ID
     * @param fileHash              The file hash which is the file name
     * @param newFilePath           The new file path to add to the cloud
     */
    private void updateFileFromCloudOperation(CloudBlobContext cloudBlobContext, String ownerID, String fileHash, String newFilePath){
        cloudBlobContext.getBlobStore().removeBlob(CONTAINER_NAME, ownerID+"/"+fileHash);
        String fileData = getFileData(newFilePath);
        ByteSource payload = ByteSource.wrap(fileData.getBytes(Charsets.UTF_8));
        try {
            String newFileHash = HashFunction.fromByteToBase58(FilesUtilities.getFileHash(fileData.getBytes()));
            String blobName = ownerID + "/" + newFileHash;
            addBlob(cloudBlobContext.getBlobStore(), blobName, payload);
            System.out.println("File added to the cloud: " + newFileHash);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cloudBlobContext.getContext().close();
    }

    /**
     * @param pathToFile            The path to the file we want to retrieve data from
     * @return String
     */
    private String getFileData(String pathToFile){
        InputStream in2 = FilesUtilities.getFileInputStream(pathToFile);
        byte[] fileData = FilesUtilities.getFileData(in2);
        //System.out.println(fileData.length); //Uncoment for debugging purposes
        return new String(fileData);
    }

    private void getCredentialsAWSS3() throws NoSuchFileFoundException {
        try (InputStream input = FilesUtilities.getFileInputStream(CONFIG_FILE_PATH)) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find " + CONFIG_FILE_PATH);
                //TODO:
                throw new NoSuchFileFoundException();
            }
            prop.load(input);
            this.credentialsAWSS3 = prop.getProperty("S3credentials");
            this.identityAWSS3 = prop.getProperty("S3identity");
        } catch (IOException ex) {
            //TODO: Handle exception properly
        }
    }

    private void getCredentialsGCP() throws NoSuchFileFoundException {
        try (InputStream input = FilesUtilities.getFileInputStream(CONFIG_FILE_PATH)) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find " + CONFIG_FILE_PATH);
                //TODO:
                throw new NoSuchFileFoundException();
            }
            prop.load(input);
            this.jsonFileGCP = prop.getProperty("GCPFileLocation");
        } catch (IOException ex) {
            //TODO: Handle exception properly
        }
    }

    /**
     * @param pathToFile            The path to the .json file that have the GCP credentials
     * @return String
     */
    private static String getCredentialFromJsonKeyFile(String pathToFile) throws NoSuchFileFoundException {
        File file = new File(pathToFile);
        try {
            String fileContents = Files.toString(file, UTF_8);
            GoogleCredentialsFromJson credentialSupplier = new GoogleCredentialsFromJson(fileContents);
            String credential = credentialSupplier.get().credential;
            return credential;
        } catch (FileNotFoundException e1){
            System.out.println("No credentials file.");
            throw new NoSuchFileFoundException();
        } catch (IOException e) {
            System.err.println("Exception reading private key from '%s': " + pathToFile);
            //e.printStackTrace();
            //System.exit(1);
            return null;
        }
    }

    /**
     * @param blobStore             Blob to store
     * @param blobName              Blob name
     * @param payload               Blob payload
     */
    private void addBlob(BlobStore blobStore, String blobName, ByteSource payload) throws IOException {
        Blob blob = blobStore.blobBuilder(blobName)
                .payload(payload)
                .contentLength(payload.size())
                .build();
        blobStore.putBlob(CONTAINER_NAME, blob);
    }

    /**
     * @param createContainer       Boolean of whether it is or not to create a new container
     * @return CloudBlobContext     Cloud blob context
     */
    private CloudBlobContext createCloudBlobContextAWSS3(boolean createContainer){
        CloudBlobContext cloudBlobContext = new CloudBlobContext();
        BlobStoreContext context = ContextBuilder.newBuilder(AWSS3_PROVIDER).credentials(identityAWSS3, credentialsAWSS3).buildView(AWSS3BlobStoreContext.class);
        BlobStore blobStore = context.getBlobStore();
        if(createContainer) {
            blobStore.createContainerInLocation(null, CONTAINER_NAME);
        }
        cloudBlobContext.setBlobStore(blobStore);
        cloudBlobContext.setContext(context);
        return cloudBlobContext;
    }

    /**
     * @param credentials           GCP credentials
     * @param createContainer       Boolean of whether it is or not to create a new container
     * @return CloudBlobContext     Cloud blob context
     */
    private CloudBlobContext createCloudBlobContextGCP(String credentials, boolean createContainer){
        CloudBlobContext cloudBlobContext = new CloudBlobContext();
        BlobStoreContext context = ContextBuilder.newBuilder(GCP_PROVIDER)
                .credentials("test-google-cloud@thesis-marcelo-jclouds.iam.gserviceaccount.com", credentials)
                .buildView(BlobStoreContext.class);
        BlobStore blobStore = context.getBlobStore();
        if(createContainer) {
            blobStore.createContainerInLocation(null, CONTAINER_NAME);
        }
        cloudBlobContext.setContext(context);
        cloudBlobContext.setBlobStore(blobStore);
        return cloudBlobContext;
    }
}

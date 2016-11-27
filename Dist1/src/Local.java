
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudsearchdomain.model.Bucket;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.InputStream;

public class Local {
	public static PropertiesCredentials Credentials;
	public static String propertiesFilePath = "/Users/yoavcohen/Documents/workspace/awstest/src/awstest/cred.properties";
	public static void main(String[] args) throws IOException {
		Credentials = new PropertiesCredentials(new FileInputStream(propertiesFilePath));
		AmazonS3 s3client = new AmazonS3Client(Credentials);
		String bucketName = "yoav-backet4";
		s3client.createBucket(bucketName);
		for (com.amazonaws.services.s3.model.Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		
		String fileName ="yoav.txt";
		s3client.putObject(new PutObjectRequest(bucketName, fileName, 
				new File("/Users/yoavcohen/Documents/workspace/awstest/src/awstest/file.txt"))
				.withCannedAcl(CannedAccessControlList.PublicRead));
}
}
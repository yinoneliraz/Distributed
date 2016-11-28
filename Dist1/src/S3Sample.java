	/*
	 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License").
	 * You may not use this file except in compliance with the License.
	 * A copy of the License is located at
	 *
	 *  http://aws.amazon.com/apache2.0
	 *
	 * or in the "license" file accompanying this file. This file is distributed
	 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
	 * express or implied. See the License for the specific language governing
	 * permissions and limitations under the License.
	 */
	import java.io.BufferedReader;
	 
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import java.io.InputStream;
	import java.io.InputStreamReader;
	import java.io.OutputStreamWriter;
	import java.io.Writer;
	import java.util.ArrayList;
	import java.util.List;

	import com.amazonaws.AmazonClientException;
	import com.amazonaws.AmazonServiceException;
	import com.amazonaws.auth.AWSCredentials;
	import com.amazonaws.auth.PropertiesCredentials;
	import com.amazonaws.partitions.model.Region;
	import com.amazonaws.regions.Regions;

	import com.amazonaws.services.ec2.AmazonEC2;
	import com.amazonaws.services.ec2.AmazonEC2Client;
	import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
	import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
	import com.amazonaws.services.ec2.model.CreateKeyPairResult;
	import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
	import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
	import com.amazonaws.services.ec2.model.CreateTagsRequest;
	import com.amazonaws.services.ec2.model.DescribeInstancesResult;
	import com.amazonaws.services.ec2.model.Instance;
	import com.amazonaws.services.ec2.model.InstanceType;
	import com.amazonaws.services.ec2.model.IpPermission;
	import com.amazonaws.services.ec2.model.KeyPair;
	import com.amazonaws.services.ec2.model.Placement;
	import com.amazonaws.services.ec2.model.Reservation;
	import com.amazonaws.services.ec2.model.RunInstancesRequest;
	import com.amazonaws.services.ec2.model.Tag;
	import com.amazonaws.services.s3.AmazonS3;
	import com.amazonaws.services.s3.AmazonS3Client;
	import com.amazonaws.services.s3.model.GetObjectRequest;
	import com.amazonaws.services.s3.model.ListObjectsRequest;
	import com.amazonaws.services.s3.model.PutObjectRequest;
	import com.amazonaws.services.s3.model.Bucket;
	import com.amazonaws.services.s3.model.S3Object;
	import com.amazonaws.services.s3.model.ObjectListing;
	import com.amazonaws.services.s3.model.S3ObjectSummary;
	 
	/**
	 * This sample demonstrates how to make basic requests to Amazon S3 using
	 * the AWS SDK for Java.
	 * <p>
	 * <b>Prerequisites:</b> You must have a valid Amazon Web Services developer
	 * account, and be signed up to use Amazon S3. For more information on
	 * Amazon S3, see http://aws.amazon.com/s3.
	 * <p>
	 * <b>Important:</b> Be sure to fill in your AWS access credentials in the
	 *                   AwsCredentials.properties file before you try to run this
	 *                   sample.
	 * http://aws.amazon.com/security-credentials
	 */

	public class S3Sample {
		public static PropertiesCredentials Credentials;
		public static String propertiesFilePath = "/Users/yoavcohen/Documents/workspace/awstest/src/awstest/cred.properties";
	    public static void main(String[] args) throws IOException {
	        /*
	         * Important: Be sure to fill in your AWS access credentials in the
	         *            AwsCredentials.properties file before you try to run this
	         *            sample.
	         * http://aws.amazon.com/security-credentials
	         */
	    	
	    	
	    	
	        Credentials = new PropertiesCredentials(new FileInputStream(propertiesFilePath));
			AmazonS3 s3 = new AmazonS3Client(Credentials);
			
			 AmazonEC2 ec2;
		        
			 Credentials = new PropertiesCredentials(new FileInputStream(propertiesFilePath));
			 ec2 = new AmazonEC2Client(Credentials);
			 getinstances(ec2);
			 
			 
			 try{
			 CreateSecurityGroupRequest csgr = new CreateSecurityGroupRequest();
			 csgr.withGroupName("JavaSecurityGroupp").withDescription("My security group");
			 CreateSecurityGroupResult createSecurityGroupResult =ec2.createSecurityGroup(csgr);
			 IpPermission ipPermission =new IpPermission();

			ipPermission.withIpRanges("79.177.216.184/32", "150.150.150.150/32")
					            .withIpProtocol("tcp")
					            .withFromPort(22)
					            .withToPort(22);
			AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =new AuthorizeSecurityGroupIngressRequest();

			authorizeSecurityGroupIngressRequest.withGroupName("JavaSecurityGroupp").withIpPermissions(ipPermission);
						                          
		
						ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
			 }
			catch (AmazonServiceException ase) {
				            System.out.println("Error Message:    " + ase.getMessage());
			}



			CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
			createKeyPairRequest.withKeyName("test6");
			try{
			CreateKeyPairResult createKeyPairResult =ec2.createKeyPair(createKeyPairRequest);
			

			KeyPair keyPair = new KeyPair();
			keyPair = createKeyPairResult.getKeyPair();
			String privateKey = keyPair.getKeyMaterial();
			
			System.out.println(privateKey);
			}
			catch (AmazonServiceException ase) {
	            System.out.println("Error Message:    " + ase.getMessage());
			}
			RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

	       	runInstancesRequest.withImageId("ami-4b814f22")
			        		                     .withInstanceType("m1.small")
			        		                     .withMinCount(1)
			        		                     .withMaxCount(1)
			        		                     .withKeyName("test6")
			        		                     .withSecurityGroups("JavaSecurityGroupp");
			List<Instance> instances = ec2.runInstances(runInstancesRequest).getReservation().getInstances();
			           
			System.out.println("Launch instances: " + instances);
			tagInstance(instances.get(0).getInstanceId(),"manage","yes",ec2);
	    }



	public static void getinstances(AmazonEC2 ec2Client){
		DescribeInstancesResult result= ec2Client.describeInstances(); 
		List<Reservation> reservations = result.getReservations();
	    for (Reservation reservation : reservations) {
	        List<Instance> instancess = reservation.getInstances();  
	        for (Instance instance : instancess) {
	            System.out.println(instance.getTags());
	        }
	    }
	}
	    
	    
	public static void tagInstance(String instanceId, String tag, String value, AmazonEC2 ec2Client) {
	    //quick fix
		
		ArrayList<Tag> arr = new ArrayList<Tag>();
		Tag tagtemp=new com.amazonaws.services.ec2.model.Tag(tag, value);
		arr.add(tagtemp);
	    try {
	        Thread.sleep(1000);
	    } catch (InterruptedException e) {
	        // swallow
	    }
	    CreateTagsRequest request = new CreateTagsRequest();
	    request = request.withResources(instanceId)
	                     .withTags(arr);
	                 
	    ec2Client.createTags(request);
	}
	}
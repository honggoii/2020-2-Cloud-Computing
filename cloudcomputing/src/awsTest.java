import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class awsTest {
	
	/* 
	 * Cloud Computing, Data Computing Laboratory
	 * Department of Computer Science
	 * 2016039028 Hyunkyeong Lee
	 * Chungbuk National University
	 */

	static AmazonEC2 ec2;
	
	private static void init() throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (~/.aws/credentials).
		 */
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file." +
					"Please make sure that your credentials file is at the correct" +		
					"location (~/.aws/credentails), and is in valid format.",
					e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
				.withCredentials(credentialsProvider)
				.withRegion("us-east-1") /*check the region at AWS console */
				.build();
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		init();
		
		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;
		
		boolean flag = true;
		
		while(flag)
		{
			System.out.println("                                                            ");
			System.out.println("                                                            ");
			System.out.println("------------------------------------------------------------");
			System.out.println("           Amazon AWS Contorl Panel using SDK               ");
			System.out.println("                                                            ");
			System.out.println("  Cloud Computing, Computer Science Department              ");
			System.out.println("                    2016039028 Hyunkyeong Lee               ");
			System.out.println("                           at Chungbuk National University  ");
			System.out.println("------------------------------------------------------------");
			System.out.println("  1. list instance                2. available zones        ");
			System.out.println("  3. start instance               4. available regions      ");
			System.out.println("  5. stop instance                6. create instance        ");
			System.out.println("  7. reboot instance              8. list images            ");
			System.out.println("  9. terminate instance          99. quit                   ");
			System.out.println("------------------------------------------------------------");
			System.out.println("Enter an integer: ");

			number = menu.nextInt(); //select menu number
			
			 // Refresh credentials using a background thread, automatically every minute. This will log an error if IMDS is down during
			 // a refresh, but your service calls will continue using the cached credentials until the credentials are refreshed
			 // again one minute later.
			 
			 
			
			switch(number) {
			case 1:
				listInstances();
				break;
			case 2:
				availableZones();
				break;
			case 3:
				startInstance();
				break;
			case 4:
				availableRegions();
				break;
			case 5:
				stopInstance();
				break;
			case 6:
				createInstance();
				break;
			case 7:
				rebootInstance();
				break;
			case 8:
				listImages();
				break;
			case 9:
				terminateInstance();
				break;
			case 99:
				flag = false;
				break;
			}
		}
		
		System.out.println("Bye!!!!");
	}

	//case 1. list instance
	public static void listInstances()	{
		System.out.println("Listing instances....");
		boolean done = false;
		
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			
			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					System.out.printf(
							"[id] %s, " +
							"[AMI] %s, " +
							"[type] %s, " +
							"[state] %10s, " +
							"[monitoring state] %s",
							instance.getInstanceId(),
							instance.getImageId(),
							instance.getInstanceType(),
							instance.getState().getName(),
							instance.getMonitoring().getState());
				}
				System.out.println();
			}
			
			request.setNextToken(response.getNextToken());
			
			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}
	
	//case 2. available zones
	public static void availableZones()	{
		System.out.println("Avaliable Zones ... ");
		
		DescribeAvailabilityZonesResult response = ec2.describeAvailabilityZones();
		
		for(AvailabilityZone zone : response.getAvailabilityZones()) {
			System.out.printf(
					"Found Availability Zone %s " +
							"with status %s " +
							"in region %s",
					zone.getZoneName(),
					zone.getState(),
					zone.getRegionName());
			System.out.println();	
		}
	}
	
	//case 3. start instance
	public static void startInstance()	{
		System.out.println("Start Instance ...");
		System.out.println("Enter instance id: ");
		
		Scanner sc = new Scanner(System.in);
		String instanceId = sc.nextLine(); //instance id
			
		StartInstancesRequest request = new StartInstancesRequest();
		request.withInstanceIds(instanceId);
		
		ec2.startInstances(request);
				
		System.out.printf("Starting .... %s\n", instanceId);
		System.out.printf("Successfully started instance %s", instanceId);
		
	}
	
	//case 4. available regions
	public static void availableRegions()	{
		System.out.println("Avaliable Regiones ... ");
		
		DescribeRegionsResult response = ec2.describeRegions();
		
		for(Region region : response.getRegions()) {
			System.out.printf(
					"Found Regions %s " + 
					"with endpoint %s",
					region.getRegionName(),
					region.getEndpoint());
		}
	}
	
	//case 5. stop instance
	public static void stopInstance() {
		System.out.println("Stop Instance ...");
		System.out.println("Enter instance id: ");
		
		Scanner sc = new Scanner(System.in);
		String instanceId = sc.nextLine(); //instance id
		
		StopInstancesRequest request = new StopInstancesRequest();
		request.withInstanceIds(instanceId);

		ec2.stopInstances(request);

		System.out.printf("Successfully stop instance %s", instanceId);
	}
	
	//case 6. create instance
	public static void createInstance() {
		System.out.println("Create instances ...");
		System.out.println("Enter ami id: ");
		
		Scanner sc = new Scanner(System.in);
		String amiId = sc.nextLine();//ami id
		
		try {
			RunInstancesRequest request = new RunInstancesRequest();
			request.withImageId(amiId);
			request.withInstanceType(InstanceType.T2Micro);
			request.withMaxCount(1);
			request.withMinCount(1);
			request.withKeyName("awscloud");
			request.withSecurityGroupIds("htcondor-security");

			RunInstancesResult response = ec2.runInstances(request);
			String instanceId = response.getReservation().getInstances().get(0).getInstanceId();
			
			System.out.printf(
					"Successfully started EC2 Instances %s based on AMI %s",
					instanceId, amiId);

		} catch (Exception e) {
			throw new AmazonClientException("Unsuccessfully started EC2 Instances");
		}
	}

	
	//case 7. reboot instance
	public static void rebootInstance() {
		System.out.println("Reboot Instances ...");
		System.out.println("Enter instance id: ");
		
		Scanner sc = new Scanner(System.in);
		String instanceId = sc.nextLine(); //instance id
		
		try {
			System.out.printf("Rebooting .... %s\n", instanceId);
			RebootInstancesRequest request = new RebootInstancesRequest();
			request.withInstanceIds(instanceId);
			
			ec2.rebootInstances(request);
			System.out.printf("Successfully rebooted instance %s", instanceId);
		} catch (Exception e) {
			throw new AmazonClientException("Unsuccessfully rebooted instance");
		}
	}
	
	//case 8. list images
	public static void listImages()	{
		System.out.println("Listing images....");
		String owner = "664259704595"; 
		
		DescribeImagesRequest request = new DescribeImagesRequest().withOwners(owner);
		
		Collection<Image> response = ec2.describeImages(request).getImages();

		for(Image image : response) {
			System.out.printf(
					"[ImageID] %s, " +
					"[Name] %s, " +
					"[Owner] %s, ",
					image.getImageId(),
					image.getName(),
					image.getOwnerId());
			System.out.println();
		}
	}
	
	//case 9. terminate instance
	public static void terminateInstance() {
		System.out.println("Treminate Instance ...");
		System.out.println("Enter instance id: ");
		
		Scanner sc = new Scanner(System.in);
		String instanceId = sc.nextLine(); //instance id
		
		TerminateInstancesRequest request = new TerminateInstancesRequest();
		request.withInstanceIds(instanceId);
		
		ec2.terminateInstances(request);
		System.out.printf("Successfully terminate instance %s", instanceId);
	}
}

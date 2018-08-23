package project3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class Project3 {
	private static Vector<Integer> frameSize = new Vector<Integer>();
	private static Vector<Integer> fileAvgs = new Vector<Integer>();

	private static Vector<Double> timeVec = new Vector<Double>();
	private static Vector<Double> timeAvg = new Vector<Double>();

	private static Vector<Integer> listFrameAvgs = new Vector<Integer>();//these 2 keep the avgs across all read files
	private static Vector<Double> listTimeAvgs = new Vector<Double>();
	
	static int rerun = 0;//only want to run a few times to see if we get new data

	public static void main(String[] args){
		Project3 reader = new Project3();

		//here we decide to look at packet size or frames/sec
		//0 is frames/sec
		//1 is bytes/frame
		//2 is both
		int frameOrSizeFlag = 2;

		//run same methods on each file in data dir if more than one
		File folder = new File("C:/Users/Public/Documents/UB/CSE664/Project 3/Project 3/Data");
		String filePath = "C:/Users/Public/Documents/UB/CSE664/Project 3/Project 3/Data";
		File[] filesList = folder.listFiles();
		//for(File file : filesList){

		//timeVec.add(0, null);
		//frameSize.add(0, null);

		for(int i=0;i<filesList.length;i++){	
			if(filesList[i].isFile()){
				if(frameOrSizeFlag == 0){
					timeAvg.setSize(10);

					System.out.println("File we are going to read is " + filesList[i].getName());
					reader.boop(filePath + "/" + filesList[i].getName());	
					System.out.println("min time diff b/t frames is " + reader.minimum());
					System.out.println("max time diff b/t frames is " + reader.maximum());
					//System.out.println(i);
					timeAvg.add(i, reader.average());
					System.out.println("average frames/sec is " + timeAvg.get(i));

					if(rerun <1){//use data to determine how to detect if someone is in ip camera
						//should probably pass it all the details it will need
						//first it needs the avg with no movement
						//then it needs to read a new file and compare the 
						//avg vals every x frames to the average
						//needs to take time into account which is why x should
						//be large ~100 frames/sec (check on all data and get an avg)
						//reader.movement();
					}

					timeVec.clear();
					timeAvg.clear();
				}
				else if(frameOrSizeFlag ==1){
					fileAvgs.setSize(10);
					System.out.println("File we are going to read is " + filesList[i].getName());
					reader.beep(filePath + "/" + filesList[i].getName());
					System.out.println("min frame size is " + reader.min());
					System.out.println("max frame size is " + reader.max());
					fileAvgs.add(i, reader.avg());
					System.out.println("avg frame size is " + fileAvgs.get(i));

					if(rerun < 1){
						//reader.movement();
					}

					frameSize.clear();
					fileAvgs.clear();
				}
				else{
					timeAvg.setSize(10);

					System.out.println("File we are going to read is " + filesList[i].getName());
					reader.boop(filePath + "/" + filesList[i].getName());	
					System.out.println("min time diff b/t frames is " + reader.minimum());
					System.out.println("max time diff b/t frames is " + reader.maximum());
					//System.out.println(i);
					timeAvg.add(i, reader.average());
					listTimeAvgs.add(i, reader.average());
					System.out.println("average frames/sec is " + timeAvg.get(i));


					fileAvgs.setSize(10);
					//System.out.println("File we are going to read is " + filesList[i].getName());
					reader.beep(filePath + "/" + filesList[i].getName());
					System.out.println("min frame size is " + reader.min());
					System.out.println("max frame size is " + reader.max());
					fileAvgs.add(i, reader.avg());
					listFrameAvgs.add(i, reader.avg());
					System.out.println("avg frame size is " + fileAvgs.get(i));

					if(rerun < 1 && i==5){//for now
						/*hard coding the pair of files we need to look at
						 *since i only have one pair, it is hardcoded
						 *other assumptions can be made to fix this like
						 *each pair of files must be without mvmt and with
						 *or one could specifiy in the console and then those 
						 *entries could be set into vars, but only for one pair each run
						 */
						int avgFrames = (int) Math.round(listTimeAvgs.get(i-1));
						//System.out.println(avgFrames);
						//System.out.println(listFrameAvgs.get(i-1));
						boolean target5 = reader.movement(avgFrames, listFrameAvgs.get(i-1));
						if(target5){
							System.out.println("There is movement in your camera for file " 
									+ filesList[i].getName());
						}
						else{
							System.out.println("There is no movement in your camera for file "
									+ filesList[i].getName());
						}
					}
				}
			
				timeVec.clear();
				timeAvg.clear();
				frameSize.clear();
				fileAvgs.clear();
				System.out.println("");
			}
		}

		System.out.println("Done.");
		if (rerun < 5){
			try {
				TimeUnit.SECONDS.sleep(15);
				rerun++;
				//reset for next round
				frameSize.clear();
				fileAvgs.clear();

				timeVec.clear();
				timeAvg.clear();

				Project3.main(args);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	//this method uses data from a file to tell whether or not
	//someone is in the frame
	//to do this we average out every x frames to an int and their sizes
	//compare this to the average from 0 movement that we obtained
	//and if it differs by y, we return true
	public boolean movement(int avgFrames, int avgBytes) {
		Vector<Integer> moveVec = new Vector<Integer>();
		int avg = 0;
		//get all the avg bytes/sec for whole file
		//and add them to a vector
		for(int j=1; j<frameSize.size(); j++){
			for(int k=j; k<avgFrames;k++){
				//System.out.println(frameSize.get(k));
				avg += frameSize.get(k);
				if(k+1<frameSize.size()){
					j=k+1;
				}
			}
			moveVec.add(avg/avgFrames);
			//System.out.println(avg/avgFrames);
			avg = 0;
		}
		//now we go through the vector and compare 
		//all values to the average size to see if we get
		//higher than Y bytes
		for(int u=0; u<moveVec.size();u++){
			if(moveVec.get(u) > avgBytes){
				return true;
			}
			else return false;
		}
		/*
		BufferedReader reader = null;
		String line = "";
		String splitby = ",";
		int i=0;
		try{
			reader = new BufferedReader(new FileReader(filePath));
			while((line = reader.readLine()) != null) {
				if(i==0){
					//timeVec.add(i, null);
					i++;
				}
				else{
					String[] cvsline = line.split(splitby);
					//System.out.println(cvsline[1]);
					String number = cvsline[1].substring(1, cvsline[1].length()-1);

					//timeVec.add(i, Double.parseDouble(number));

					i++;
				}
			}
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			if (reader != null){
				try{
					reader.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		 */

		return false;//default for now
	}
	//this method goes through a cvs file and extracts the no. of frames and 
	//their size (in bytes) so we can run statistics and gather assumptions on this data
	public void boop(String filePath){
		//String file = "C:/Users/Public/Documents/UB/CSE664/Project 3/Project 3/Data/data001";
		/*		Files.walk(Paths.get("C:/User/Public/Documents/UB/CSE664/Project 3/Project 3/Data")).forEach(filePath -> {
			if(Files.isRegularFile(filePath)){
				System.out.println(filePath);
			}
		});
		 */
		BufferedReader reader = null;
		String line = "";
		String splitby = ",";
		int i=0;
		try{
			reader = new BufferedReader(new FileReader(filePath));
			while((line = reader.readLine()) != null) {
				if(i==0){
					timeVec.add(i, null);
					i++;
				}
				else{
					String[] cvsline = line.split(splitby);
					//System.out.println(cvsline[1]);
					String number = cvsline[1].substring(1, cvsline[1].length()-1);
					//System.out.println(number);
					//System.out.println("frame size in strings is " + number);
					//need to convert them to integers, or at least cvsline[5]

					//try {
					//frameSize.add(Integer.parseInt(number), i);
					//int stringint = Integer.parseInt(number);
					//System.out.println("before adding to list, " + Integer.parseInt(number));
					//System.out.println(i);
					timeVec.add(i, Double.parseDouble(number));
					//} catch (NumberFormatException e){
					//	e.printStackTrace();
					//}
					//		valueOf(cvsline[5]), i);s
					//System.out.println("frame " + i + " is size " + frameSize.get(i));
					i++;
					//System.out.println("Testing parse, frame number is " + cvsline[0] + " size is " + cvsline[5] + " bytes");
				}
			}
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			if (reader != null){
				try{
					reader.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		//System.out.println("Done");
	}
	//this method gives us the smallest time difference
	//between consecutive frames
	public double minimum(){
		double ret = timeVec.get(2)-timeVec.get(1);//1 is always 0, can never be less than that
		//System.out.println("ret val is " + ret);
		//System.out.println( "largest int is " + Integer.MAX_VALUE );
		for(int k = 3; k<timeVec.size();k++){
			//System.out.println(k);
			//System.out.println(timeVec.get(k));
			//System.out.println(timeVec.get(k-1));
			if ((timeVec.get(k)-timeVec.get(k-1) < ret) && ((timeVec.get(k)-timeVec.get(k-1)) > 0)){
				ret = timeVec.get(k)-timeVec.get(k-1);
				//System.out.println("at index " + k);
				//System.out.println("changing ret val to " + ret);
			}
		}

		return ret;
	}
	//this method gives us the largest time difference
	//between consecutive frames
	public double maximum(){
		double ret = timeVec.get(2)-timeVec.get(1);
		for(int k = 3; k<timeVec.size();k++){
			if (timeVec.get(k)-timeVec.get(k-1) > ret){
				ret = timeVec.get(k)-timeVec.get(k-1);
			}
		}

		return ret;
	}
	public double average(){
		double ret;
		double avg = timeVec.get(timeVec.size()-1);//should be last time value in vec
		ret = timeVec.size() / avg;
		return ret;
	}
	/*
	 * each frame index corresponds to the vector index
	 * and its data is the size of the frame from the file
	 */
	public void beep(String filePath){
		//String file = "C:/Users/Public/Documents/UB/CSE664/Project 3/Project 3/Data/data001";
		/*		Files.walk(Paths.get("C:/User/Public/Documents/UB/CSE664/Project 3/Project 3/Data")).forEach(filePath -> {
			if(Files.isRegularFile(filePath)){
				System.out.println(filePath);
			}
		});
		 */
		BufferedReader reader = null;
		String line = "";
		String splitby = ",";
		int i=0;
		try{
			reader = new BufferedReader(new FileReader(filePath));
			while((line = reader.readLine()) != null) {
				if(i==0){
					frameSize.add(i, null);
					i++;
				}
				else{
					String[] cvsline = line.split(splitby);
					String number = cvsline[5].substring(1, cvsline[5].length()-1);
					//System.out.println(number);
					//System.out.println("frame size in strings is " + number);
					//need to convert them to integers, or at least cvsline[5]

					//try {
					//frameSize.add(Integer.parseInt(number), i);
					//int stringint = Integer.parseInt(number);
					//System.out.println("before adding to list, " + Integer.parseInt(number));
					//System.out.println(i);
					frameSize.add(i, Integer.parseInt(number));
					//} catch (NumberFormatException e){
					//	e.printStackTrace();
					//}
					//		valueOf(cvsline[5]), i);s
					//System.out.println("frame " + i + " is size " + frameSize.get(i));
					i++;
					//System.out.println("Testing parse, frame number is " + cvsline[0] + " size is " + cvsline[5] + " bytes");
				}
			}
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			if (reader != null){
				try{
					reader.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		//System.out.println("Done");
	}
	public int min(){
		int ret = frameSize.get(1);
		for(int i=2; i<frameSize.size();i++){
			if(frameSize.get(i)<ret){
				ret = frameSize.get(i);
			}
		}
		return ret;
	}
	public int max(){
		int ret = frameSize.get(1);
		for(int i=2; i<frameSize.size();i++){
			if(frameSize.get(i)>ret){
				ret = frameSize.get(i);
			}
		}
		return ret;
	}
	public int avg(){
		int ret=0;
		int sum=0;
		for(int i=1; i<frameSize.size();i++){
			sum += frameSize.get(i);
		}
		ret = sum / frameSize.size();
		return ret;
	}
	//this version of run takes the data file and prints out the no. and size of each frame
	//i need to do statistical data so i need to store it instead
	public void run(){
		String file = "C:/Users/Public/Documents/UB/CSE664/Project 3/Project 3/Data/data001";
		BufferedReader reader = null;
		String line = "";
		String splitby = ",";

		try{
			reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				String[] cvsline = line.split(splitby);

				System.out.println("Testing parse, frame number is " + cvsline[0] + " size is " + cvsline[5] + " bytes/bits");
			}
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			if (reader != null){
				try{
					reader.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		System.out.println("Done");
	}
}

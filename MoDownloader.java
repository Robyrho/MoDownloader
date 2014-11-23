import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Timer;

import org.apache.commons.lang3.time.StopWatch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.base.Stopwatch;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.ModListHelper;

@Mod(modid = MoDownloader.MODID, version = MoDownloader.VERSION)
public class MoDownloader {
    public static final String MODID = "modownloader";
    public static final String VERSION = "1.0";
    
    private String modsPath;
    private File config;
    
    @EventHandler
    public void init(FMLPreInitializationEvent event){
    	modsPath = event.getModConfigurationDirectory().getAbsolutePath().substring(0, event.getModConfigurationDirectory().getAbsolutePath().lastIndexOf(File.separator)) + File.separator + "mods";
		config = new File(event.getModConfigurationDirectory() + File.separator + "modownloader.json");
		if(!config.exists()) {
			try {
				writeFirstConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JSONParser parser = new JSONParser();
			try {
		 
				Object obj = parser.parse(new FileReader(config));
		 
				JSONObject jsonObject = (JSONObject) obj;
		 
				String name = (String) jsonObject.get("modsDownloadPath");
				System.out.println(name);
				
				JSONArray list = (JSONArray) jsonObject.get("modsUrls");
				for(int i = 0; i < list.size(); i++) {
					JSONObject mod = (JSONObject) list.get(i);
					String mName = (String) mod.get("name");
					String mLink = (String) mod.get("link");
					File modF = new File(modsPath + File.separator + mName + ".jar");
					if(!modF.exists()) {
						System.out.println("Downloading mod " + mName + "from " + mLink);
						downloadMod(mName, mLink);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
    }
    
    private void writeFirstConfig() throws IOException {
    	PrintWriter w = new PrintWriter(config);
    	w.write("{\n");
    	w.write("    \"modsDownloadPath\":\"" + modsPath + "\",\n");
    	w.write("    \"modsUrls\":[\n");
    	w.write("        {\"name\":\"TestMod\", \"link\":\"http://example.com\"}");
    	w.write("    ]\n");
    	w.write("}\n");
    	w.close();
    }
    
    private void downloadMod(String name, String url) throws IOException {
    	Stopwatch timer = Stopwatch.createStarted();
    	
		String fileName = modsPath + File.separator + name + ".jar"; //The file that will be saved on your computer
		URL link = new URL(url); //The file that you want to download
		
		//Code to download
		InputStream in = new BufferedInputStream(link.openStream());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1!=(n=in.read(buf))) {
		   out.write(buf, 0, n);
		}
		out.close();
		in.close();
		byte[] response = out.toByteArray();
		
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(response);
		fos.close();
		//End download code
		 
		System.out.println("Finished downloading of mod " + name + ". Took " + timer.stop());
		ModListHelper.additionalMods.put(name, new File(fileName));
    }
}

package com.ssomar.score.projectiles;

import com.ssomar.score.SCore;
import com.ssomar.score.projectiles.types.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProjectilesLoader {

	private static ProjectilesLoader instance;

	private int cpt;

	private List<EntityType> validProjectiles = new ArrayList<>();

	public ProjectilesLoader() {
		validProjectiles.add(EntityType.FIREBALL);
		validProjectiles.add(EntityType.ARROW);
		validProjectiles.add(EntityType.DRAGON_FIREBALL);
		validProjectiles.add(EntityType.EGG);
		validProjectiles.add(EntityType.ENDER_PEARL);
		validProjectiles.add(EntityType.SPLASH_POTION);
		validProjectiles.add(EntityType.SHULKER_BULLET);
		validProjectiles.add(EntityType.SNOWBALL);
		validProjectiles.add(EntityType.WITHER_SKULL);

		if(!SCore.is1v12()) {
			validProjectiles.add(EntityType.TRIDENT);
		}
	}

	public void load() {
		this.rewriteProjectiles();
		// ITEMS CONFIG
		ProjectilesManager.getInstance().setProjectiles(new ArrayList<SProjectiles>());

		if (new File(SCore.plugin.getDataFolder() + "/projectiles").exists()) {
			this.loadProjectilesbyFile();
		}else {
			this.createDefaultProjectilesFile();
			this.load();
		}
	}

	public void rewriteProjectiles(){
		File file = new File("plugins/ExecutableItems/projectiles");
		if(file.exists()){
			boolean pass = false;
			for(File f : file.listFiles()){
				new File("plugins/SCore/projectiles/").mkdirs();
				File transfer = new File("plugins/SCore/projectiles/"+f.getName());
				try {
					transfer.createNewFile();
					try (
							InputStream in = new BufferedInputStream(
									new FileInputStream(f));
							OutputStream out = new BufferedOutputStream(
									new FileOutputStream(transfer))) {

						byte[] buffer = new byte[1024];
						int lengthRead;
						while ((lengthRead = in.read(buffer)) > 0) {
							out.write(buffer, 0, lengthRead);
							out.flush();
						}
					}
					f.delete();
					pass = true;
				} catch (IOException e) {
					e.printStackTrace();
				}

				if(pass) {
					try {
						new File("plugins/ExecutableItems/projectiles/NOW_THE_PROJECTILES_ARE_IN_SCORE").createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public List<String> getDefaultProjectilesName(boolean isPremium) {
		List<String> defaultProjectiles = new ArrayList<>();
		defaultProjectiles.add("arrow1");
		if(!isPremium) {
			defaultProjectiles.add("egg1");
			defaultProjectiles.add("shulkerbullet1");
			defaultProjectiles.add("enderpearl1");
			defaultProjectiles.add("trident1");
			defaultProjectiles.add("fireball1");
			defaultProjectiles.add("lingering1");
			defaultProjectiles.add("snowball1");
			defaultProjectiles.add("witherskull1");
			defaultProjectiles.add("type");
			defaultProjectiles.add("tridentCustom1");
		}
		return defaultProjectiles;
	}

	public void createDefaultProjectilesFile() {
		SCore.plugin.getServer().getLogger().severe("[SCore] CANT LOAD YOUR PROJECTILES, FOLDER 'projectiles' not found !");
		SCore.plugin.getServer().getLogger().severe("[SCore] DEFAULT PROJECTILES CREATED !");

		List<String> defaultProjectilesLoaded = this.getDefaultProjectilesName(false);

		for(String id : defaultProjectilesLoaded) {
			try {
				File pdfile = new File(SCore.plugin.getDataFolder() + "/projectiles/", id + ".yml");
				InputStream in = this.getClass().getResourceAsStream("/com/ssomar/score/configs/projectiles/" + id + ".yml");

				if(!pdfile.exists()){
					SCore.plugin.getDataFolder().mkdirs();
					pdfile.getParentFile().mkdirs();
					pdfile.createNewFile();
				}else return;

				OutputStream out = new FileOutputStream(pdfile);
				byte[] buffer = new byte[1024];
				int current = 0;

				while((current = in.read(buffer)) > -1) out.write(buffer, 0, current);

				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void loadProjectilesbyFile() {
		cpt = 0;
		List<String> listFiles = Arrays.asList(new File(SCore.plugin.getDataFolder() + "/projectiles").list());
		Collections.sort(listFiles);

		for (String s : listFiles) {
			File fileEntry = new File(SCore.plugin.getDataFolder() + "/projectiles/"+s);
			if (fileEntry.isDirectory()) loadProjectilesInFolder(fileEntry);
			else {
				if(!fileEntry.getName().contains(".yml")) continue;
				String id = fileEntry.getName().split(".yml")[0];
				
				if(id.equals("type")) continue;

				SProjectiles projectile;
				if((projectile = this.getProjectileByFile(fileEntry, id, true)) == null) {
					SCore.plugin.getServer().getLogger().severe("[SCore] Couldn't load the projectile associate with the file "+s);
					continue;
				}
				ProjectilesManager.getInstance().addProjectile(projectile);
				cpt++;
				SCore.plugin.getServer().getLogger().info("[SCore] projectile " + id + " was loaded !");
			}
		}
	}

	public void loadProjectilesInFolder(File folder) {
		List<String> listFiles = Arrays.asList(folder.list());
		Collections.sort(listFiles);

		for (String s : listFiles) {
			File fileEntry = new File(folder +"/"+s);
			if (fileEntry.isDirectory()) loadProjectilesInFolder(fileEntry);
			else {
				if(!fileEntry.getName().contains(".yml")) continue;
				String id = fileEntry.getName().split(".yml")[0];

				SProjectiles projectile;
				if((projectile = this.getProjectileByFile(fileEntry, id, true)) == null) {
					SCore.plugin.getServer().getLogger().severe("[SCore] Couldn't load the projectile associate with the file"+s);
					continue;
				}
				ProjectilesManager.getInstance().addProjectile(projectile);
				cpt++;
				SCore.plugin.getServer().getLogger().info("[SCore] projectile " + id + " was loaded !");
			}
		}
	}

	public static File searchFileOfProjectile(String id) {

		List<String> listFiles = Arrays.asList(new File(SCore.plugin.getDataFolder() + "/projectiles").list());
		Collections.sort(listFiles);

		for (String s : listFiles) {
			File fileEntry = new File(SCore.plugin.getDataFolder() + "/projectiles/"+s);
			if (fileEntry.isDirectory()) {
				File result = null;
				if((result = searchFileOfProjectileInFolder(id, fileEntry))==null) continue;
				else return result;
			}
			else {
				if(!fileEntry.getName().contains(".yml")) continue;
				String currentId= fileEntry.getName().split(".yml")[0];
				if(id.equals(currentId)) return fileEntry;

			}
		}
		return null;
	}

	public static File searchFileOfProjectileInFolder(String id, File folder) {
		List<String> listFiles = Arrays.asList(folder.list());
		Collections.sort(listFiles);

		for (String s : listFiles) {
			File fileEntry = new File(folder +"/"+s);
			if (fileEntry.isDirectory()) {
				File result =null;
				if((result= searchFileOfProjectileInFolder(id, fileEntry))==null) continue;
				else return result;
			}
			else {
				if(!fileEntry.getName().contains(".yml")) continue;
				String currentId= fileEntry.getName().split(".yml")[0];
				if(id.equals(currentId)) return fileEntry;
			}
		}
		return null;
	}

	@Nullable
	public SProjectiles getProjectileByFile(File file, String id, boolean showError) {

		try {
				FileConfiguration projConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(file);

				if(projConfig.contains("type")) {	
					EntityType entityType;
					if(validProjectiles.contains((entityType = EntityType.valueOf(projConfig.getString("type").toUpperCase())))) {
						SProjectiles projectile = null;

						switch(entityType){
							case SPLASH_POTION:
								projectile = new CustomLingering(id, file);
								break;
							case ARROW:
								projectile = new CustomArrow(id, file);
								break;
							case EGG:
								projectile = new CustomEgg(id, file);
								break;
							case ENDER_PEARL:
								projectile = new CustomEnderpearl(id, file);
								break;
							case FIREBALL: case DRAGON_FIREBALL: case SMALL_FIREBALL:
								projectile = new CustomFireball(id, file);
								break;
							case SHULKER_BULLET:
								projectile = new CustomShulkerBullet(id, file);
								break;
							case SNOWBALL:
								projectile = new CustomSnowball(id, file);
								break;
							case TRIDENT:
								projectile = new CustomTrident(id, file);
								break;
							case WITHER_SKULL:
								projectile = new CustomWitherSkull(id, file);
								break;
						}
						return projectile;
					}
					else {
						SCore.plugin.getLogger().severe("[ExecutableItems] Invalid type of projectile for the projectile file: "+file.getName());
						return null;
					}
				}
				else {
					return null;
				}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public SProjectiles getProjectileByID(String id, boolean showError) {
		try {
			File file = searchFileOfProjectile(id);
			return getProjectileByFile(file, id, showError);
		}catch(Exception e) {
			return null;
		}
	}


	public List<String> getAllProjectiles() {

		ArrayList<String> result = new ArrayList<>();
		if (new File(SCore.plugin.getDataFolder() + "/projectiles").exists()) {
			List<String> listFiles = Arrays.asList(new File(SCore.plugin.getDataFolder() + "/projectiles").list());
			Collections.sort(listFiles);

			for (String s : listFiles) {
				File fileEntry = new File(SCore.plugin.getDataFolder() + "/projectiles/"+s);
				if (fileEntry.isDirectory()) result.addAll(getAllProjectilesOfFolder(fileEntry));
				else {
					if(!fileEntry.getName().contains(".yml")) continue;
					String id= fileEntry.getName().split(".yml")[0];

					result.add(id);
				}
			}
		}
		return result;
	}

	public List<String> getAllProjectilesOfFolder(File folder){
		ArrayList<String> result = new ArrayList<>();

		List<String> listFiles = Arrays.asList(folder.list());
		Collections.sort(listFiles);

		for (String s : listFiles) {
			File fileEntry = new File(folder +"/"+s);
			if (fileEntry.isDirectory()) result.addAll(getAllProjectilesOfFolder(fileEntry));
			else {
				if(!fileEntry.getName().contains(".yml")) continue;
				String id= fileEntry.getName().split(".yml")[0];

				result.add(id);
			}
		}

		return result;
	}

	public String getStringBeforeEnd(String insert) {
		StringBuilder sb = new StringBuilder("");
		for (char c : insert.toCharArray()) {
			if (c == ',' || c == '}') {
				return sb.toString();
			} else
				sb.append(c);
		}
		return "";
	}

	public void reload() {
		load();
	}

	public static ProjectilesLoader getInstance() {
		if (instance == null) instance = new ProjectilesLoader();
		return instance;
	}

}
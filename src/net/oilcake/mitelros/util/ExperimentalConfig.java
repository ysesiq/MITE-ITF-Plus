package net.oilcake.mitelros.util;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ExperimentalConfig {
    public static Map<String, ConfigItem> Tags = new HashMap<>();

    public static class ConfigItem<T>{
        public String ConfigKey;
        public T ConfigValue;
        public T min;
        public T max;
        public boolean isNeedCompare = false;
        public String ConfigComment;
        ConfigItem(String key, T value, String comment){
            this.ConfigKey = key;
            this.ConfigValue = value;
            this.ConfigComment = comment;
        }
        ConfigItem(String key, T value, String comment, T min, T max){
            this.ConfigKey = key;
            this.ConfigValue = value;
            this.isNeedCompare = true;
            this.min = min;
            this.max = max;
            this.ConfigComment = comment + " [范围："+ min + "-" + max +"]";
        }
        public void setConfigValue(T configValue) {
            ConfigValue = configValue;
        }
        public T getConfigValue() {
            return this.ConfigValue;
        }
    }

    public static class TagConfig {
        public static ConfigItem <Boolean> TagCreaturesV2 = new ConfigItem<>("CreaturesV2", true, "新动物生成机制 \n// 动物成群生成");
        public static ConfigItem <Boolean> TagSpawningV2 = new ConfigItem<>("SpawningV2",true,"新怪物生成频率 \n// 主世界怪物刷新量随主世界的时间变化而变化,正午怪物刷新量最少，午夜怪物刷新量最多");
        public static ConfigItem <Boolean> TagBenchingV2 = new ConfigItem<>("BenchingV2",false,"工作站废料回收  \n// 工作站挖掘后不掉落自身,只掉落合成材料,掉落的数量与耐久挂钩");
        public static ConfigItem <Boolean> FinalChallenge = new ConfigItem<>("FinalChallenge",false,"终极挑战模式  \n// 挑战难度越高,怪物的强度也就越高;挑战难度越低,则反之");
        public static ConfigItem <Boolean> Realistic = new ConfigItem<>("Realistic",false,"真实状态模拟" +
                "  \n// 1.在玩家生命值低于5点时，玩家将不能获得疾跑速度加成、无法跳跃，同时移动速度和挖掘速度将按剩余生命值比例降低，且出现视野黑暗效果" +
                "  \n// 2.玩家食用生肉会使玩家饥饿" +
                "  \n// 3.玩家无需跳跃即可爬上1格高的障碍且玩家在允许跳跃时不允许攀爬墙壁" +
                "  \n// 4.玩家在没有饱食度的情况下将无法跳跃" +
                "  \n// 5.动物在生病时每5分钟会受到1点伤害" +
                "  \n// 6.怪物的攻击距离判定点更改为其碰撞箱的2/3高处" +
                "  \n// 7.玩家的最低点和最高点同时纳入攻击判定内" +
                "  \n// 8.怪物将享受100%的武器攻击距离加成" +
                "  \n// 9.玩家在周围亮度过低时移动速度减缓，除非自身具有夜视效果" +
                "  \n// 10.玩家在减速超过50%的情况下将无法起跳" +
                "  \n// 11.食用肉饥饿的概率提升");
        public static ConfigItem <Boolean> SeasonColor = new ConfigItem<>("SeasonColor",true,"季节植被颜色  \n// 季节将影响草方块，草，树叶，藤蔓的颜色");
        //*这个有问题*//

        //public static ConfigItem <Boolean> = new ConfigItem("Tag",false,"(LVL)");
    }

    public static void loadConfigs(){
        System.out.println("Experimental settings were put in HASHMAP");
        //常驻
        Tags.put("CreaturesV2",TagConfig.TagCreaturesV2);
        Tags.put("SpawningV2",TagConfig.TagSpawningV2);
        Tags.put("BenchingV2",TagConfig.TagBenchingV2);
        Tags.put("FinalChallenge",TagConfig.FinalChallenge);
        Tags.put("Realistic", TagConfig.Realistic);
        Tags.put("SeasonColor", TagConfig.SeasonColor);
//      Tags.put("NoWeatherPredict",TagConfig.TagNoWeatherPredict);


        String filePth = "ExperimentalOption.cfg";
        File file_mite = new File(filePth);
        if (file_mite.exists()) {
            System.out.println("READING SETTINGS FILE");
            Properties properties = new Properties();
            FileReader fr = null;
            try {
                fr = new FileReader(file_mite.getName());
                properties.load(fr);
                fr.close();
                readConfigFromFile(file_mite, properties);
                packConfigFile(file_mite, properties);
            } catch (FileNotFoundException var6) {
                System.out.println("READING FAILED TP1");
                var6.printStackTrace();
            } catch (IOException var7) {
                System.out.println("READING FAILED TP2");
                var7.printStackTrace();
            }
        } else {
            System.out.println("GENERATING SETTINGS FILE");
            try {
                if (file_mite.createNewFile()){
                    file_mite.setExecutable(true);//设置可执行权限
                    file_mite.setReadable(true);//设置可读权限
                    file_mite.setWritable(true);//设置可写权限
                    generateConfigFile(file_mite);
                }
            } catch (IOException e) {
                System.out.println("GENERATING FAILED");
                e.printStackTrace();
                JFrame jFrame = new JFrame();
                jFrame.setAlwaysOnTop(true);
                JOptionPane.showMessageDialog(jFrame, "实验性玩法读取失败，尝试删除配置文件……", "错误", 0);
                System.exit(0);
            }
        }
    }

    public static void  readConfigFromFile(File file_mite, Properties properties) {
        for (String key : properties.stringPropertyNames()) {
            ConfigItem configItem = Tags.get(key);
            if(configItem != null) {
                if(configItem.ConfigValue instanceof Boolean) {
                    configItem.setConfigValue(Boolean.parseBoolean(properties.getProperty(key)));
                } else if(configItem.ConfigValue instanceof Float) {
                    float value = Float.parseFloat(properties.getProperty(key));
                    if(configItem.isNeedCompare) {
                        value = value > (float)configItem.max ? (float) configItem.max : Math.max(value, (float) configItem.min);
                    }
                    configItem.setConfigValue(value);
                } else if(configItem.ConfigValue instanceof Double) {
                    double value = Double.parseDouble(properties.getProperty(key));
                    if(configItem.isNeedCompare) {
                        value = value > (double)configItem.max ? (double) configItem.max : Math.max(value, (double) configItem.min);
                    }
                    configItem.setConfigValue(value);
                } else if(configItem.ConfigValue instanceof Integer) {
                    int value = Integer.parseInt(properties.getProperty(key));
                    if(configItem.isNeedCompare) {
                        value = value > (int)configItem.max ? (int) configItem.max : Math.max(value, (int) configItem.min);
                    }
                    configItem.setConfigValue(value);
                } else {
                    configItem.setConfigValue(properties.getProperty(key));
                }
            }
        }
    }

    public static void packConfigFile(File file,Properties properties) {
        try{
            FileWriter fileWritter = new FileWriter(file.getName(), true);
            for (Map.Entry<String, ConfigItem> entry: Tags.entrySet()) {
                ConfigItem value = entry.getValue();
                String localValue = properties.getProperty(value.ConfigKey);
                if(localValue == null) {
                    fileWritter.write("// " + value.ConfigComment + "\n");
                    fileWritter.write(value.ConfigKey + "=" + value.ConfigValue + "\n\n");
                }
            }
            fileWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateConfigFile(File file) {
        try{
            FileWriter fileWritter = new FileWriter(file.getName());
            fileWritter.write("// 在每一项配置后填入true或者false来选择，不建议在游玩中途更改设置 \n");
            for (Map.Entry<String, ConfigItem> entry: Tags.entrySet()) {
                ConfigItem value = entry.getValue();
                fileWritter.write("// " + value.ConfigComment + "\n");
                fileWritter.write(value.ConfigKey + "=" + value.ConfigValue + "\n\n");
            }
            fileWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

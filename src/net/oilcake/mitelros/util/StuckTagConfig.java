package net.oilcake.mitelros.util;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StuckTagConfig {
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
        public static ConfigItem <Boolean> TagLoseBearings = new ConfigItem<>("LoseBearings", false, "(LVL1)迷失方向：删除游戏右上角的所有显示");
        public static ConfigItem <Boolean> TagHeatStroke = new ConfigItem<>("HeatStroke",false,"(LVL1)酷暑代价：水分自然消耗的速度提升100%");
        public static ConfigItem <Boolean> TagInstinctSurvival = new ConfigItem<>("InstinctSurvival",false,"(LVL1)防御本能：怪物享受护甲防御的比率提升25%，同时取消保底1伤害的设定");
        public static ConfigItem <Boolean> TagFallenInMineLVL1 = new ConfigItem<>("FallenInMineLVL1",false,"(LVL1)矿难群体：主世界矿洞生成僵尸扈从的概率提升");
        public static ConfigItem <Boolean> TagBattleSufferLVL1 = new ConfigItem<>("BattleSufferLVL1",false,"(LVL1)久经沙场：主世界矿洞生成骷髅侍卫的概率提升");
        public static ConfigItem <Boolean> TagFallenInMineLVL2 = new ConfigItem<>("FallenInMineLVL2",false,"(LVL2)矿难群体：主世界矿洞生成僵尸扈从的概率提升，亡魂的生命值提升50%，攻击力提升25%，且召唤僵尸支援");
        public static ConfigItem <Boolean> TagBattleSufferLVL2 = new ConfigItem<>("BattleSufferLVL2",false,"(LVL2)久经沙场：主世界矿洞生成骷髅侍卫的概率提升，骷髅领主的生命值提升50%，攻击力提升40%，召唤的支援获得强化");
        public static ConfigItem <Boolean> TagInvisibleFollower = new ConfigItem<>("InvisibleFollower",false,"(LVL1)无形跟随：更低层数的爬行者将被替换为潜伏爬行者");
        public static ConfigItem <Boolean> TagLegendFreeze = new ConfigItem<>("LegendFreeze",false,"(LVL1)刺骨寒风：寒冷惩罚的积累速度提升200%");
        public static ConfigItem <Boolean> TagUnstableConvection = new ConfigItem<>("UnstableConvection",false,"(LVL1)不稳定对流：闪电的触发频率提升300%");
        public static ConfigItem <Boolean> TagEternalRaining = new ConfigItem<>("EternalRaining",false,"(LVL2)阴雨连绵：雨的最长持续时间提升300%，最短持续时间提升700%");
        public static ConfigItem <Boolean> TagDryDilemma = new ConfigItem<>("DryDilemma",false,"(LVL1)旱地：降低非碗类食物回复含水量的能力（奇数去尾，等于1更改概率）");
        public static ConfigItem <Boolean> TagDeadgeothermy = new ConfigItem<>("DeadGeothermy",false,"(LVL2)地热失效：地下世界成为寒冷生物群系，更改地下世界基岩生成，同时生成绿宝石");
        public static ConfigItem <Boolean> TagHeatStorm = new ConfigItem<>("HeatStorm",false,"(LVL1)灼地烈阳：玩家额外拥有炎热惩罚");
        public static ConfigItem <Boolean> TagApocalypse = new ConfigItem<>("Apocalypse",false,"(LVL3)灾厄余生：不再自然生成可提供肉类的动物");
        public static ConfigItem <Boolean> TagArmament = new ConfigItem<>("Armament",false,"(LVL-2)战备军械：玩家的护甲值在耐久低于25%时才会减少，且不再受到低于自身护甲值的伤害");
        public static ConfigItem <Boolean> TagDistortion = new ConfigItem<>("Distortion",false,"(LVL-2)血肉畸变：玩家可获得最高40的生命值");
        public static ConfigItem <Boolean> TagWorshipDark = new ConfigItem<>("WorshipDark",false,"(LVL2)崇尚黑暗：僵尸将尝试摧毁其沿途可见的火把");
        public static ConfigItem <Boolean> TagDemonDescend = new ConfigItem<>("DemonDescend",false,"(LVL2)恶魔降临：僵尸猪人领主血量提升50%，攻击力提升25%，小概率手持钨钉头锤，且会召唤猪人守卫支援");
        public static ConfigItem <Boolean> TagMiracleDisaster = new ConfigItem<>("MiracleDisaster",false,"(LVL1)迷幻危机：出现更多种类怪物的刷怪笼");
        public static ConfigItem <Boolean> TagPseudovision = new ConfigItem<>("Pseudovision",false,"(LVL1)幻视暗示：黑色食尸鬼在成功索敌玩家后会给予玩家一次视觉黑暗效果");
        public static ConfigItem <Boolean> TagDimensionInvade = new ConfigItem<>("DimensionInvade",false,"(LVL4)维度入侵：除了末地之外的任何维度都会生成其他维度的敌对生物");
        public static ConfigItem <Boolean> TagRejection = new ConfigItem<>("Rejection",false,"(LVL2)世界排异：玩家始终获得一种女巫诅咒，尝试消除诅咒将随机改变诅咒类型");
        public static ConfigItem <Boolean> TagUnderAlliance = new ConfigItem<>("UnderAlliance", false, "(LVL1)蛰骨联盟：出现更多种类的骷髅骑士");
        public static ConfigItem <Boolean> TagDigest = new ConfigItem<>("Digest", false, "(LVL-2)原生代谢：玩家食用生肉/饮用水获得概率性debuff的概率降低100%");

        //*这个有问题*//
        public static ConfigItem <Boolean> TagAcousma = new ConfigItem<>("Acousma",false,"(LVL1)幻听暗示：玩家依照y轴位置会随机产生一些幻听");
        public static ConfigItem <Boolean> TagNoWeatherPredict = new ConfigItem<>("NoWeatherPredict", false, "(LVL1)阴晴无定：删除天气预报");
        public static ConfigItem <Boolean> TagUnstableExplosion = new ConfigItem<>("UnstableExplosion",false,"(LVL1)不稳定爆炸：潜伏爬行者的爆炸半径都将变得随机，但是不会低于原来的爆炸半径");
        //public static ConfigItem <Boolean> = new ConfigItem("Tag",false,"(LVL)");
    }

    public static void loadConfigs(){
        System.out.println("Tags Were put in HASHMAP");
        //常驻
        Tags.put("DryDilemma",TagConfig.TagDryDilemma);
        Tags.put("HeatStroke",TagConfig.TagHeatStroke);
        Tags.put("InstinctSurvival",TagConfig.TagInstinctSurvival);
        Tags.put("LegendFreeze",TagConfig.TagLegendFreeze);
        Tags.put("HeatStorm",TagConfig.TagHeatStorm);
        Tags.put("Rejection",TagConfig.TagRejection);

        //临时
        Tags.put("FallenInMineLVL1",TagConfig.TagFallenInMineLVL1);
        Tags.put("BattleSufferLVL1",TagConfig.TagBattleSufferLVL1);
        Tags.put("FallenInMineLVL2",TagConfig.TagFallenInMineLVL2);
        Tags.put("BattleSufferLVL2",TagConfig.TagBattleSufferLVL2);
        Tags.put("InvisibleFollower",TagConfig.TagInvisibleFollower);
        Tags.put("UnstableConvection",TagConfig.TagUnstableConvection);
        Tags.put("EternalRaining",TagConfig.TagEternalRaining);
        Tags.put("DeadGeothermy",TagConfig.TagDeadgeothermy);
        Tags.put("Apocalypse",TagConfig.TagApocalypse);
        Tags.put("Armament",TagConfig.TagArmament);
        Tags.put("Distortion",TagConfig.TagDistortion);
        Tags.put("WorshipDark",TagConfig.TagWorshipDark);
        Tags.put("DemonDescend",TagConfig.TagDemonDescend);
        Tags.put("LoseBearings",TagConfig.TagLoseBearings);
        Tags.put("MiracleDisaster",TagConfig.TagMiracleDisaster);
        Tags.put("Pseudovision",TagConfig.TagPseudovision);
        Tags.put("DimensionInvade",TagConfig.TagDimensionInvade);
        Tags.put("UnderAlliance",TagConfig.TagUnderAlliance);
        Tags.put("Digest",TagConfig.TagDigest);

        //删除
//      Tags.put("NoWeatherPredict",TagConfig.TagNoWeatherPredict);
//      Tags.put("Acousma",TagConfig.TagAcousma);
//      Tags.put("UnstableExplosion",TagConfig.TagUnstableExplosion);


        String filePth = "StuckChallenge.cfg";
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
                JOptionPane.showMessageDialog(jFrame, "词条读取失败，尝试删除配置文件……", "错误", 0);
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

package com.ipast.permission.utils.rom;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.ipast.permission.utils.CloseUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Properties;

import static android.os.Build.VERSION_CODES.O;
import static android.os.Build.VERSION_CODES.P;


public final class ROMUtils {

    private ROMUtils() {
    }

    // ===============
    // = ROM 标识信息 =
    // ===============

    private static final String[] ROM_HUAWEI = {"huawei"};
    private static final String[] ROM_VIVO = {"vivo"};
    private static final String[] ROM_XIAOMI = {"xiaomi"};
    private static final String[] ROM_OPPO = {"oppo"};
    private static final String[] ROM_LEECO = {"leeco", "letv"};
    private static final String[] ROM_360 = {"360", "qiku"};
    private static final String[] ROM_ZTE = {"zte"};
    private static final String[] ROM_ONEPLUS = {"oneplus"};
    private static final String[] ROM_NUBIA = {"nubia"};
    private static final String[] ROM_COOLPAD = {"coolpad", "yulong"};
    private static final String[] ROM_LG = {"lg", "lge"};
    private static final String[] ROM_GOOGLE = {"google"};
    private static final String[] ROM_SAMSUNG = {"samsung"};
    private static final String[] ROM_MEIZU = {"meizu"};
    private static final String[] ROM_LENOVO = {"lenovo"};
    private static final String[] ROM_SMARTISAN = {"smartisan", "deltainno"};
    private static final String[] ROM_HTC = {"htc"};
    private static final String[] ROM_SONY = {"sony"};
    private static final String[] ROM_GIONEE = {"gionee", "amigo"};
    private static final String[] ROM_MOTOROLA = {"motorola"};

    private static final String VERSION_PROPERTY_HUAWEI = "ro.build.version.emui";
    private static final String VERSION_PROPERTY_VIVO = "ro.vivo.os.build.display.id";
    private static final String VERSION_PROPERTY_XIAOMI = "ro.build.version.incremental";
    private static final String VERSION_PROPERTY_OPPO = "ro.build.version.opporom";
    private static final String VERSION_PROPERTY_LEECO = "ro.letv.release.version";
    private static final String VERSION_PROPERTY_360 = "ro.build.uiversion";
    private static final String VERSION_PROPERTY_ZTE = "ro.build.MiFavor_version";
    private static final String VERSION_PROPERTY_ONEPLUS = "ro.rom.version";
    private static final String VERSION_PROPERTY_NUBIA = "ro.build.rom.id";

    /**
     * 判断 ROM 是否 Huawei ( 华为 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isHuawei() {
        return ROM_HUAWEI[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Vivo ( VIVO )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isVivo() {
        return ROM_VIVO[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Xiaomi ( 小米 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isXiaomi() {
        return ROM_XIAOMI[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Oppo ( OPPO )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isOppo() {
        return ROM_OPPO[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Leeco ( 乐视 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isLeeco() {
        return ROM_LEECO[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 360 ( 360 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean is360() {
        return ROM_360[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Zte ( 中兴 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isZte() {
        return ROM_ZTE[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Oneplus ( 一加 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isOneplus() {
        return ROM_ONEPLUS[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Nubia ( 努比亚 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isNubia() {
        return ROM_NUBIA[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Coolpad ( 酷派 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isCoolpad() {
        return ROM_COOLPAD[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Lg ( LG )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isLg() {
        return ROM_LG[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Google ( 谷歌 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isGoogle() {
        return ROM_GOOGLE[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Samsung ( 三星 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isSamsung() {
        return ROM_SAMSUNG[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Meizu ( 魅族 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isMeizu() {
        return ROM_MEIZU[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Lenovo ( 联想 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isLenovo() {
        return ROM_LENOVO[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Smartisan ( 锤子 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isSmartisan() {
        return ROM_SMARTISAN[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Htc ( HTC )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isHtc() {
        return ROM_HTC[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Sony ( 索尼 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isSony() {
        return ROM_SONY[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Gionee ( 金立 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isGionee() {
        return ROM_GIONEE[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 判断 ROM 是否 Motorola ( 摩托罗拉 )
     *
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isMotorola() {
        return ROM_MOTOROLA[0].equalsIgnoreCase(getRomInfo().getName());
    }

    /**
     * 获取 ROM 信息
     *
     * @return {@link RomInfo}
     */
    public static RomInfo getRomInfo() {
        if (mRomInfo == null) {
            mRomInfo = execGetRomInfo();
        }
        return mRomInfo;
    }


    private static final String UNKNOWN = "unknown";

    /**
     * 是否匹配正确 ROM
     *
     * @param brand        产品 / 硬件品牌信息
     * @param manufacturer 产品 / 硬件制造商信息
     * @param names        品牌名称集合
     * @return {@code true} yes, {@code false} no
     */
    private static boolean isRightRom(final String brand, final String manufacturer, final String... names) {
        for (String name : names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取 ROM 版本信息
     *
     * @param propertyName 属性名
     * @return ROM 版本信息
     */
    private static String getRomVersion(final String propertyName) {
        String version = "";
        if (!TextUtils.isEmpty(propertyName)) {
            version = getSystemProperty(propertyName);
        }
        if (TextUtils.isEmpty(version) || version.equals(UNKNOWN)) {
            version = Build.DISPLAY;
        }
        if (TextUtils.isEmpty(version)) {
            return UNKNOWN;
        }
        return version;
    }

    /**
     * 获取 system prop 文件指定属性信息
     *
     * @param name 属性名
     * @return system prop 文件指定属性信息
     */
    private static String getSystemProperty(final String name) {
        String prop = getSystemPropertyByShell(name);
        if (!TextUtils.isEmpty(prop)) {
            return prop;
        }
        prop = getSystemPropertyByStream(name);
        if (!TextUtils.isEmpty(prop)) {
            return prop;
        }
        if (Build.VERSION.SDK_INT < P) {
            return getSystemPropertyByReflect(name);
        }
        return prop;
    }

    /**
     * 通过 shell 方式获取 system prop 文件指定属性信息
     *
     * @param propName 属性名
     * @return system prop 文件指定属性信息
     */
    private static String getSystemPropertyByShell(final String propName) {
        BufferedReader reader = null;
        Process mProcess = null;
        try {
            mProcess = Runtime.getRuntime().exec("getprop " + propName);
            reader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()), 1024);
            String ret = reader.readLine();
            if (ret != null) {
                return ret;
            }
        } catch (Exception e) {
        } finally {
            CloseUtils.closeQuietly(reader);
            if (mProcess != null) {
                mProcess.destroy();
            }
        }
        return "";
    }

    /**
     * 获取 system prop 文件指定属性信息
     *
     * @param key 属性 key
     * @return system prop 文件指定属性信息
     */
    private static String getSystemPropertyByStream(final String key) {
        FileInputStream fis = null;
        try {
            Properties prop = new Properties();
            fis = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            prop.load(fis);
            return prop.getProperty(key, "");
        } catch (IOException e) {
        } finally {
            CloseUtils.closeQuietly(fis);
        }
        return "";
    }

    /**
     * 获取 system prop 文件指定属性信息
     *
     * @param key 属性 key
     * @return system prop 文件指定属性信息
     */
    @SuppressLint("PrivateApi")
    private static String getSystemPropertyByReflect(final String key) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method getMethod = clz.getMethod("get", String.class, String.class);
            return (String) getMethod.invoke(clz, key, "");
        } catch (Exception e) {

        }
        return "";
    }


    private static RomInfo mRomInfo = null;

    /**
     * 获取 ROM 信息
     *
     * @return {@link RomInfo}
     */
    private static RomInfo execGetRomInfo() {
        RomInfo romInfo = new RomInfo();
        String brand = Build.BRAND;
        String manufacturer = Build.MANUFACTURER;
        if (isRightRom(brand, manufacturer, ROM_HUAWEI)) {
            romInfo.setName(ROM_HUAWEI[0]);
            String version = getRomVersion(VERSION_PROPERTY_HUAWEI);
            String[] temp = version.split("_");
            if (temp.length > 1) {
                romInfo.setVersion(temp[1]);
            } else {
                romInfo.setVersion(version);
            }
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_VIVO)) {
            romInfo.setName(ROM_VIVO[0]);
            romInfo.setVersion(getRomVersion(VERSION_PROPERTY_VIVO));
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_XIAOMI)) {
            romInfo.setName(ROM_XIAOMI[0]);
            romInfo.setVersion(getRomVersion(VERSION_PROPERTY_XIAOMI));
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_OPPO)) {
            romInfo.setName(ROM_OPPO[0]);
            romInfo.setVersion(getRomVersion(VERSION_PROPERTY_OPPO));
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_LEECO)) {
            romInfo.setName(ROM_LEECO[0]);
            romInfo.setVersion(getRomVersion(VERSION_PROPERTY_LEECO));
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_360)) {
            romInfo.setName(ROM_360[0]);
            romInfo.setVersion(getRomVersion(VERSION_PROPERTY_360));
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_ZTE)) {
            romInfo.setName(ROM_ZTE[0]);
            romInfo.setVersion(getRomVersion(VERSION_PROPERTY_ZTE));
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_ONEPLUS)) {
            romInfo.setName(ROM_ONEPLUS[0]);
            romInfo.setVersion(getRomVersion(VERSION_PROPERTY_ONEPLUS));
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_NUBIA)) {
            romInfo.setName(ROM_NUBIA[0]);
            romInfo.setVersion(getRomVersion(VERSION_PROPERTY_NUBIA));
            return romInfo;
        }
        if (isRightRom(brand, manufacturer, ROM_COOLPAD)) {
            romInfo.setName(ROM_COOLPAD[0]);
        } else if (isRightRom(brand, manufacturer, ROM_LG)) {
            romInfo.setName(ROM_LG[0]);
        } else if (isRightRom(brand, manufacturer, ROM_GOOGLE)) {
            romInfo.setName(ROM_GOOGLE[0]);
        } else if (isRightRom(brand, manufacturer, ROM_SAMSUNG)) {
            romInfo.setName(ROM_SAMSUNG[0]);
        } else if (isRightRom(brand, manufacturer, ROM_MEIZU)) {
            romInfo.setName(ROM_MEIZU[0]);
        } else if (isRightRom(brand, manufacturer, ROM_LENOVO)) {
            romInfo.setName(ROM_LENOVO[0]);
        } else if (isRightRom(brand, manufacturer, ROM_SMARTISAN)) {
            romInfo.setName(ROM_SMARTISAN[0]);
        } else if (isRightRom(brand, manufacturer, ROM_HTC)) {
            romInfo.setName(ROM_HTC[0]);
        } else if (isRightRom(brand, manufacturer, ROM_SONY)) {
            romInfo.setName(ROM_SONY[0]);
        } else if (isRightRom(brand, manufacturer, ROM_GIONEE)) {
            romInfo.setName(ROM_GIONEE[0]);
        } else if (isRightRom(brand, manufacturer, ROM_MOTOROLA)) {
            romInfo.setName(ROM_MOTOROLA[0]);
        } else {
            romInfo.setName(manufacturer);
        }
        romInfo.setVersion(getRomVersion(""));
        return romInfo;
    }
}
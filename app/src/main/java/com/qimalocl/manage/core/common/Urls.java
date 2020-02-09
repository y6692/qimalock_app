package com.qimalocl.manage.core.common;

/**
 * 请求地址帮助类
 *
 * @author LiDongYao
 *
 * @version v1.0 2016-4-7
 */
public class Urls {

	public static String HTTP = "http://";
	public static String HTTPS = "https://";
	public static String host = HTTP + "app.7mate.cn";
//	public static String host = HTTP + "uat.7mate.cn";
	public static String host2 = HTTPS + "newmapi.7mate.cn/api";

	//获取验证码接口
	public static String verificationcode = host2 + "/verificationcode";
	//注册登录接口、退出登录接口delete
	public static String authorizations = host2 + "/authorizations";
	//用户信息接口
	public static String user = host2 + "/user";

	//上锁接口
	public static String lock = host2 + "/car/lock";
	//开锁接口
	public static String unlock = host2 + "/car/unlock";


	/**上传头像*/
	public static String uploadsheadImg = host + "/index.php?g=App&m=User&a=uploadsheadImg";

	/**存入设备信息*/
	public static String DevicePostUrl = host + "/index.php?g=App&m=Login&a=verifyDevice_info";
	/**账号密码登录*/
	public static String loginNormal = host + "/index.php?g=App&m=Login&a=loginNormalDdy";
	/**扫码用车*/
	public static String useCar = host + "/index.php?g=App&m=User&a=useCarDdy";
	/**扫码获取锁信息（不开锁）*/
	public static String lockInfo = host + "/index.php?g=App&m=UserManage&a=lock_info";
	/**小安中控入库*/
	public static String add_xiaoan_car = host + "/index.php?g=App&m=Test&a=add_xiaoan_car";

	public static String useCar2 = host + "/index.php?g=App&m=User&a=useCar";
	/**获取启动页图广告*/
	public static String getIndexAd = host + "/index.php?g=App&m=Index&a=getIndexAd";
	/**上报位置*/
	public static String upcarmap = host + "/index.php?g=App&m=User&a=upcarmap";
	/**附近车接口*/
	public static String nearby = host + "/index.php?g=App&m=Index&a=nearby";
	/**附近的电单车接口*/
	public static String nearbyEbike = host + "/index.php?g=App&m=Index&a=nearbyEbike";
    /**附近的电单车接口2*/
    public static String nearbyEbikeScool = host + "/index.php?g=App&m=Index&a=nearbyEbikeScool";
	/**坏车列表*/
	public static String badcarList = host + "/index.php?g=App&m=UserManage&a=badcar_list";
	/**坏车详情*/
	public static String badcarShow = host + "/index.php?g=App&m=UserManage&a=badcar_show";
	/**历史订单*/
	public static String historys = host + "/index.php?g=App&m=UserManage&a=historys";
	/**扣分接口*/
	public static String delpoints = host + "/index.php?g=App&m=UserManage&a=delpoints";
	/**车辆位置*/
	public static String carsLocation = host + "/index.php?g=App&m=UserManage&a=cars";
	/**提交密钥*/
	public static String changeKey = host + "/index.php?g=App&m=UserManage&a=pdk";
	/**提交密码*/
	public static String changePsd = host + "/index.php?g=App&m=UserManage&a=pwd";
	/**泺平锁加密接口*/
	public static String rent = host + "/index.php?g=App&m=Index&a=rent";
	/**入库*/
	public static String addCar = host + "/index.php?g=App&m=Index&a=addblueCar";
	/**回收*/
	public static String recycle = host + "/index.php?g=App&m=UserManage&a=update_car_callback_lock";
	/**解锁*/
	public static String unLock = host + "/index.php?g=App&m=UserManage&a=update_car_status";
	/**结束用车*/
	public static String endCar = host + "/index.php?g=App&m=UserManage&a=normal_back_car";
	/**已修好*/
	public static String hasRepaired = host + "/index.php?g=App&m=UserManage&a=update_edit_bad";
	/**车辆报废接口*/
	public static String set_car_scrapped = host + "/index.php?g=App&m=UserManage&a=set_car_scrapped";

	/**锁定*/
//	public static String lock = host + "/index.php?g=App&m=UserManage&a=update_car_status_lock";		//TODO

	/**打开电池锁接口*/
	public static String battery_unlock = host + "/index.php?g=App&m=Ebike&a=battery_unlock";

	/**电单车信息接口*/
	public static String ebikeInfo = host + "/index.php?g=App&m=Ebike&a=info";

	/**电单车关锁*/
	public static String closeEbikeDdy = host + "/index.php?g=App&m=User&a=closeEbikeDdy";
	/**电单车寻车接口*/
	public static String ddSearch = host + "/index.php?g=App&m=Ebike&a=ddSearch";

    /**学校范围电子栅栏*/
    public static String schoolRange = host + "/index.php?g=App&m=SchoolRange&a=index";
	/**创建电子围栏接口*/
	public static String addSchoolRange = host + "/index.php?g=App&m=SchoolRange&a=store";

	/**电单车gps轨迹接口*/
	public static String gpstrack = host + "/index.php?g=App&m=Ebike&a=gpstrack";
	/**用户信息*/
	public static String userIndex = host + "/index.php?g=App&m=User&a=userIndex";


	/**预警列表接口*/
	public static String alarm_lists = host + "/index.php?g=App&m=UserManage&a=alarm_lists";
	/**处理预警接口*/
	public static String handle_alarm = host + "/index.php?g=App&m=UserManage&a=handle_alarm";
	/**电单车撤防接口*/
	public static String close_defend = host + "/index.php?g=App&m=Ebike&a=close_defend";
	/**电单车设防接口*/
	public static String open_defend = host + "/index.php?g=App&m=Ebike&a=open_defend";
}

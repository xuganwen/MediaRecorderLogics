package com.xuganwen.mediaapplication;

/**
 * 文件描述：  权限代理类，非必须实现
 * 作者：徐干稳
 * 创建时间：2019/3/14
 * 更改时间：2019/3/14
 * 版本号：1.0
 */
public interface IPermissionProxy {

     void requstAppPermission();

     boolean checkAppPermission();
}

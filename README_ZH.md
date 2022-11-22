# Health Kit 扩展能力服务客户端示例代码
## 目录
* [简介](#简介)
* [开发准备](#开发准备)
* [环境要求](#环境要求)
* [操作结果](#操作结果)
* [授权许可](#授权许可)
## 简介
华为运动健康扩展能力服务（Extended Health Kit）是华为提供的一项针对运动健康能力开放的服务。通过集成Health Kit扩展能力服务，开放者可以访问华为Health Kit平台来获取运动健康的数据。

Extended Health Kit的示例代码简单描述了Extended Health Kit的所有API。本示例代码只提供了API的简单使用，仅供参考或受限使用。

华为运动健康扩展能力服务功能如下：

1. 操作运动健康的数据
2. 实时数据注册

Extended Health Kit开发详细信息， 请参考下面的链接：

https://developer.huawei.com/consumer/cn/hms/huaweihealth

## 开发准备

在使用Extended Health Kit示例代码之前，请检查IDE环境是否已安装：

1. 解压缩示例代码包。
2. 将代码包复制到IDE目录并将其导入IDE工具。
3. 另外，需要生成签名证书指纹并将证书文件添加到项目中，然后将配置添加到build.gradle。详细信息([应用证书指纹](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/signing-fingerprint-0000001059050363?ha_source=hms1)，[导入证书指纹、修改包名、配置JDK编译版本](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/change-0000001050170085))
4. AppId信息：在应用的AndroidManifest.xml文件中添加在开发者联盟创建应用时生成的AppId信息。
5. 点击“Sync Project with Gradle Files”，以确保开发环境构建完成。

## 环境要求

* Android Studio 3.6.1 及以上版本
* Java SDK 1.8 及以上版本
* 华为运动健康App 12.1.6.300 及以上版本
## 操作结果
  <img src="home.jpg" width="50%">
## 授权许可

Health Kit 扩展能力服务客户端示例代码在Apache许可证2.0版下获得许可使用。
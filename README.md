# flying-fish-gateway（飞鱼网关系统）

#### 介绍

基于spring-cloud-gateway开发的网关路由服务，用于服务与服务之间的通讯边界管理，支持过滤器、熔断、鉴权、限流、日志、监控等功能

解决目前项目中A==>B,A==>C,A==>...,服务与服务之间以直链访问的方式，改为A==>gateway==>B\C\...，服务走网关访问另一个服务，使得服务通讯可控、可复用、统一管理、安全鉴权、服务能力保护等效果

#### 特色

1.  完全支持动态网关路由配置，在控制台界面修改完毕，即可生效
2.  支持可动态配置IP、ID、TOKEN过滤器
3.  支持可动态配置全局、自定义熔断器，实现业务高峰抗压能力
4.  支持可动态配置IP、URL、REQUESTID等基于令牌桶算法自定义限流器，实现业务峰值固化，避免雪崩效应
4.  支持可动态配置HEADER、IP、请求参数、时间、Cookie等鉴权验证，加强业务访问安全，避免或减少每个业务服务重复实现安全鉴权规则

#### 流程图
![网络拓扑图](https://oscimg.oschina.net/oscnet/up-eb3ae5a6e7c95e3f28ac4bc666e6455ea9a.png "网络拓扑")
（网络拓扑）
![执行流程图](https://oscimg.oschina.net/oscnet/up-060f6ce5895acaad0bb35dafca4c76ecf1e.png "执行流程")
（执行流程）
1.  前置nginx用于负载均衡，将请求代理分发到不同网关；
2.  gateway网关路由对请求进行鉴权、限流、熔断、日志等；
3.  gateway网关路由对注册服务进行管理与路由转发；

#### 软件架构

软件架构说明

本工程共分六个模块：
1.  dynameic-gateway为核心网关路由服务，提供客户端请求转发，服务端地址路由功能，以及过滤器、熔断、鉴权、限流、日志、监控等功能
2.  dynameic-manage为界面可视化管理后台，提供客户端管理、服务端管理、IP访问管理等功能
3.  formwork为核心框架，提供基础与公共业务处理模块与类
4.  eureka-server为注册与发现服务，只需启动即可，无其它业务操作
5.  admin-server为springboot服务提供监控管理，支持可视化WEBUI，只需启动即可，无其它业务操作
6.  examples为示例项目

后续开发计划：
1.  增加全局日志输出，按一定格式存储，方便后续引入elk做数据分析
2.  增加临控功能，支持大厅全局图表展示，与单个服务、单个客户端等访问量、流量、错误等监控图表展示


#### 安装教程

1.  依赖spring-boot 2.0.3.RELEASE、spring-cloud Finchley.RELEASE版本，注意版本的搭配
2.  需安装eureke、consul等其中任意一种，也可无需注册中心运行
3.  详细安装文档：**[doc/flying-fish-gateway-说明文档.docx](https://gitee.com/omsgit/flying-fish-gateway/tree/snapshot.v.1.0/doc)** 

#### 使用说明

1.  jdk1.8 +
2.  mysql.5.x + 
3.  redis3.x +

#### 前端项目

1. 本项目采用前后端分离，此git仓库为纯后台模块
2. 点击此链接下载，前端控制台管理项目 **[flying-fish-manage](https://gitee.com/omsgit/flying-fish-manage)** 

#### 前端界面
![负载管理](https://oscimg.oschina.net/oscnet/up-69a24d02ef07aa936fb08d975094fc091f9.png "负载管理")
(负载管理)
![服务详情](https://oscimg.oschina.net/oscnet/up-59965a0a5c4a81408c39ed05e455d1cd92e.png "服务详情")
（服务详情）
![注册服务](https://oscimg.oschina.net/oscnet/up-a6bf4b28a70bebfad8a64ec06ca3048f7ab.png "注册服务")
（注册服务）
![创建网关服务](https://oscimg.oschina.net/oscnet/up-87c5da8c27e917e913569c22089b65e989c.png "创建网关服务")
（创建网关服务）
![IP名单](https://oscimg.oschina.net/oscnet/up-4f65dc499db0ac4031b94f86e8d4c93737f.png "IP名单")
（IP名单）
![接口文档](https://oscimg.oschina.net/oscnet/up-a5fad9cf38978d22150fb633e367583d720.png "接口文档")

[参见个人说明文章](https://my.oschina.net/u/437309?tab=newest&catalogId=7056744)

#### 更新记录

 ++++++++ **snapshot.v.2.0** 
1. 代码优化，已知问题题修复
2. 扩展功能：新增接口访问统计功能
3. 优化：客户端名称可修改。
注：前端需要同步更新到v.2.0

 ++++++++ **snapshot.v.1.0** 
1. 代码优化，已知问题题修复
2. 扩展功能：接口文档，提供API接口文档描述功能
3. 添加限流拦截之后的通用规范json格式输出

 ++++++++ **master** 
1. 初始版本
2. 建立基础功能：负载管理、服务管理、客户端管理、IP名单管理
3. 其它底层开发


#### 参与贡献

1.  本项目为个人开发，基于实际应用场景设计与架构，满足内部业务代理网关需求与使用
2.  欢迎有兴趣的朋友下载、学习、使用，如有问题请在issues中留言
3.  本项目完全免费开源，可自行修改、编辑、另行发版与使用，不受任何商业限制（保不保留原作者信息，无所谓了）


# flying-fish-gateway（飞鱼网关系统）

#### 介绍

基于spring-cloud-gateway开发的网关路由服务，用于服务与服务之间的通讯边界管理，支持过滤器、熔断、鉴权、限流、日志、监控等功能

解决目前项目中A==>B,A==>C,A==>...,服务与服务之间以直链访问的方式，改为A==>gateway==>B\C\...，服务走网关访问另一个服务，使得服务通讯可控、可复用、统一管理、安全鉴权、服务能力保护等效果


#### 软件架构

软件架构说明

本工程共分四个模块：
1.  dynameic-gateway为核心网关路由服务，提供客户端请求转发，服务端地址路由功能，以及过滤器、熔断、鉴权、限流、日志、监控等功能
2.  dynameic-manage为界面可视化管理后台，提供客户端管理、服务端管理、IP访问管理等功能
3.  formwork为核心框架，提供基础与公共业务处理模块与类
4.  eureka-server为注册与发现服务，只需启动即可，无其它业务操作

后续开发计划：
1.  增加全局日志输出，按一定格式存储，方便后续引入elk做数据分析
2.  增加临控功能，支持大厅全局图表展示，与单个服务、单个客户端等访问量、流量、错误等监控图表展示


#### 安装教程

1.  依赖spring-boot 2.0.3.RELEASE、spring-cloud Finchley.RELEASE版本，注意版本的搭配
2.  需安装eureke、consul等其中任意一种，也可无需注册中心运行

#### 使用说明

1.  jdk1.8 +
2.  mysql.5.x + 
3.  redis3.x +

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)

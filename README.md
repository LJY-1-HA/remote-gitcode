# 订餐管理系统（Android · Java）

一个基于 **Android + Java + SQLite** 的订餐管理系统，包含 **顾客端（点餐）** 和 **商家管理端** 双端功能，数据全部存储在本地 SQLite 数据库，无需服务器，开箱即用。

## 功能清单 

### 顾客端（底部导航：点餐 / 购物车 / 订单 / 我的）
- 注册 / 登录 / 自动登录 / 退出登录
- 点餐：菜品搜索、分类筛选（标签横滑）、加购（购物车角标实时更新）
- 购物车：数量加减、删除商品、合计金额实时计算
- 结算：填写地址 / 电话 / 备注，事务下单，下单成功跳转订单页
- 订单：查看历史订单与明细，「待接单」状态可取消
- 我的：维护手机号与常用地址（下单自动带出）

### 管理端（底部导航：菜品管理 / 订单管理 / 数据统计）
- 菜品管理：新增 / 编辑 / 删除菜品，上架下架开关，分类 / 价格 / Emoji 图标 / 描述
- 订单管理：查看全部订单（顾客、地址、明细、备注），状态流转 **接单 → 出餐完成 → 确认送达**，可取消订单
- 数据统计：今日订单、今日营业额、累计订单、累计营业额、注册用户数、菜品数、销量 TOP5 排行

## 技术栈
- 语言：Java
- 最低支持：Android 7.0（minSdk 24），目标 Android 13（targetSdk 33）
- 构建：AGP 7.4.1 + Gradle 8.0（腾讯云 Gradle 镜像 + 阿里云 Maven 镜像），适配 Android Studio 2022（内置 JDK 11）
- UI：Material Components + RecyclerView + Fragment + BottomNavigationView + ViewBinding
- 存储：SQLite（`SQLiteOpenHelper` 建表 + 预置种子数据，DAO 分层操作）
- 会话：SharedPreferences（自动登录）

> ⚠️ 重要：项目路径必须全部为英文（不能含中文）。AGP 在 Windows 上检测到路径含非 ASCII 字符会直接报
> `Failed to apply plugin 'com.android.internal.application'` 并拒绝构建，这也是本项目从
> `Android项目用例` 目录迁移到 `D:\class\vs Android\FoodOrder` 的原因。

## 运行方式
1. 用 **Android Studio** 打开本项目目录（`订餐管理系统`）；
2. 等待 Gradle 同步完成（已配置腾讯云 Gradle 发行镜像 + 阿里云 Maven 镜像，国内网络可直接同步）；
3. 连接真机或启动模拟器，点击 Run（▶）。

> 若提示缺少 SDK / JDK，按 Android Studio 弹窗引导安装即可。

## 测试账号（首次运行自动预置）
| 角色 | 用户名 | 密码 |
| ---- | ------ | ---- |
| 管理员 | admin | 123456 |
| 顾客 | user | 123456 |

也可以自行注册顾客账号（管理员不可注册）。

## 项目结构
```
app/src/main/java/com/example/foodorder/
├── model/          实体类：User、Dish、CartItem、Order、OrderItem
├── db/             DBHelper（建表+种子数据）、UserDao、DishDao、CartDao、OrderDao
├── adapter/        列表适配器：菜品、购物车、订单（顾客/管理）、菜品管理、销量排行
├── fragment/       顾客端 4 页 + 管理端 3 页
├── ui/             Login / Register / Main / Admin / Checkout / DishEdit
└── util/           SessionManager（会话）、MoneyUtil（金额）、StatusUi（订单状态样式）

app/src/main/res/
├── layout/         19 个布局（6 个 Activity + 7 个 Fragment + 6 个列表项）
├── drawable/       矢量图标 + 形状背景（无图片资源，Emoji 做菜品图标）
├── menu/           底部导航菜单（顾客端 / 管理端）
└── values/         主题、颜色、字符串、分类数组
```

## 数据库说明（food_order.db，共 5 张表）
- `users` 用户表（role：0 顾客 / 1 管理员）
- `dishes` 菜品表（available：1 上架 / 0 下架）
- `cart` 购物车表（同一用户同一菜品唯一，数量累加）
- `orders` 订单表（status：0 待接单 / 1 制作中 / 2 配送中 / 3 已完成 / -1 已取消）
- `order_items` 订单明细（菜品名称、价格做快照，菜品修改/删除不影响历史订单）

下单使用数据库事务：写订单 → 写明细 → 累加销量 → 清空购物车，保证一致性。

## 卸载重置数据
应用数据存储在本地数据库，卸载重装即可回到初始预置数据。

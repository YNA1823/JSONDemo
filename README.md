# JSONDemo - 接口自动化测试项目

基于 JSONPlaceholder API 的接口自动化测试框架，使用 Java + RestAssured + TestNG + Allure。

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 11+ |
| 测试框架 | TestNG |
| 接口测试 | RestAssured |
| 报告 | Allure |
| 数据处理 | Jackson (JSON/YAML) |
| 数据驱动 | Apache POI (Excel) |
| 数据库 | MySQL |
| 日志 | Log4j2 |
| 断言 | AssertJ |

## 项目结构

```
JSONDemo/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/demo/jsondemo/
│   │   │   ├── config/              # 配置管理
│   │   │   │   ├── ConfigManager.java
│   │   │   │   ├── ApiConfig.java
│   │   │   │   └── DatabaseConfig.java
│   │   │   ├── model/               # 数据模型
│   │   │   │   └── Post.java
│   │   │   └── utils/               # 工具类
│   │   │       ├── ApiUtils.java
│   │   │       ├── DatabaseUtils.java
│   │   │       └── ExcelReader.java
│   │   └── resources/
│   │       ├── config/config.yaml   # 配置文件
│   │       ├── testdata/posts.xlsx  # Excel测试数据
│   │       ├── sql/init.sql         # 数据库初始化脚本
│   │       └── log4j2.xml           # 日志配置
│   └── test/
│       └── java/com/demo/jsondemo/
│           ├── base/                # 基础测试类
│           │   └── BaseTest.java
│           └── tests/               # 测试用例
│               ├── PostsTests.java           # CRUD接口测试
│               ├── PostsDataDrivenTests.java # 数据驱动测试
│               └── DatabaseTests.java        # 数据库集成测试
```

## 功能特性

### 已实现

- [x] RESTful API CRUD 测试
- [x] YAML 配置文件读取
- [x] Excel 数据驱动测试
- [x] TestNG 参数化测试
- [x] Allure 测试报告
- [x] Log4j2 日志记录
- [x] MySQL 测试数据存储
- [x] MySQL 测试结果记录
- [x] 预编译 SQL 防注入

## 快速开始

### 前置条件

- JDK 11+
- Maven 3.6+
- MySQL 8.0+ (可选)
- IDEA (推荐)

### 1. 克隆项目并导入

```bash
# IDEA 直接打开项目目录即可
# 等待 Maven 自动下载依赖
```

### 2. 安装 Lombok 插件

IDEA → Settings → Plugins → 搜索 "Lombok" → Install

Settings → Build → Compiler → Annotation Processors → 勾选 Enable

### 3. 配置数据库 (可选)

```bash
# 登录 MySQL 执行初始化脚本
mysql -u root -p < src/main/resources/sql/init.sql
```

修改 `src/main/resources/config/config.yaml`：

```yaml
database:
  enabled: true
  url: "jdbc:mysql://localhost:3306/test_demo"
  username: "root"
  password: "你的密码"
```

### 4. 创建测试数据文件

在 `src/main/resources/testdata/` 创建 `posts.xlsx`：

| userId | title | body |
|--------|-------|------|
| 1 | Test Title 1 | Test Body 1 |
| 1 | Test Title 2 | Test Body 2 |

### 5. 运行测试

```bash
# 运行所有测试
mvn clean test

# 生成并打开 Allure 报告
mvn allure:serve
```

或在 IDEA 中右键 `testng.xml` → Run

## 测试报告

运行测试后：

```bash
mvn allure:serve
```

报告包含：
- 测试用例详情
- 请求/响应日志
- 测试步骤
- 失败截图

## 配置说明

### config.yaml

```yaml
api:
  baseUrl: "https://jsonplaceholder.typicode.com"  # API基础地址
  timeout: 10000                                    # 超时时间(ms)

database:
  enabled: false                                    # 是否启用数据库
  url: "jdbc:mysql://localhost:3306/test_demo"
  username: "root"
  password: "123456"
```

## 项目亮点

1. **分层设计**：配置、模型、工具、测试分离，结构清晰
2. **数据驱动**：支持 Excel 和数据库两种数据源
3. **日志完善**：控制台 + 文件双输出，便于排查问题
4. **报告美观**：Allure 报告支持步骤、附件、截图
5. **数据库集成**：测试数据可从 MySQL 读取，测试结果自动入库
6. **SQL 安全**：预编译语句防止 SQL 注入

## 后续计划

- [ ] 接入 GitHub Actions CI
- [ ] 添加失败重试机制
- [ ] 增加更多接口测试 (Users, Comments)
- [ ] 添加接口性能测试
- [ ] 集成 Jenkins

## 联系方式

作者：[你的名字]
日期：2024

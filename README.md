# JSONDemo - 接口自动化测试项目

基于 JSONPlaceholder API 的接口自动化测试框架，使用 Java + RestAssured + TestNG + Allure。

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 11 |
| 测试框架 | TestNG |
| 接口测试 | RestAssured |
| 报告 | Allure |
| 数据处理 | Jackson (JSON/YAML) |
| 数据驱动 | Apache POI (Excel) |
| 数据库 | MySQL |
| 日志 | Log4j2 |
| 断言 | AssertJ |
| CI/CD | GitHub Actions |

## 项目结构

```
JSONDemo/
├── .github/workflows/
│   └── test.yml                     # GitHub Actions CI配置
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
│   │   │       ├── ExcelReader.java
│   │   │       ├── ExcelWriter.java
│   │   │       └── TestDataGenerator.java  # 测试数据生成器
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
│               ├── PostsDataDrivenTests.java # Excel数据驱动测试
│               ├── DatabaseTests.java        # 数据库集成测试
│               └── DataGeneratorTests.java   # 数据生成器测试
```

## 功能特性

- [x] RESTful API CRUD 测试
- [x] YAML 配置文件读取
- [x] Excel 数据驱动测试
- [x] TestNG 参数化测试
- [x] Allure 测试报告
- [x] Log4j2 日志记录
- [x] MySQL 测试数据存储与结果记录
- [x] 预编译 SQL 防注入
- [x] 测试数据生成器工具
- [x] GitHub Actions CI/CD

## 测试数据生成器

自主开发的测试工具，支持多种数据生成场景：

```java
// 生成随机正常数据
Post post = TestDataGenerator.generateRandomPost();

// 批量生成
List<Post> posts = TestDataGenerator.generateRandomPosts(10);

// 生成边界值数据（空串、超长、特殊字符等）
List<Post> boundaryPosts = TestDataGenerator.generateBoundaryPosts();

// 生成异常数据（null、类型错误等）
List<Map<String, Object>> invalidData = TestDataGenerator.generateInvalidPosts();

// 导出到 Excel
ExcelWriter.exportPostsToExcel(posts, "target/data.xlsx");
```

## 快速开始

### 前置条件

- JDK 11
- Maven 3.6+
- MySQL 8.0+ (可选)
- IDEA (推荐)

### 1. 克隆项目

```bash
git clone https://github.com/YNA1823/JSONDemo.git
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

## CI/CD

项目已集成 GitHub Actions，实现自动化测试：

- **触发条件**：push/PR 到 main 分支
- **自动执行**：运行全部测试用例
- **报告上传**：Allure 报告自动上传为 Artifact

查看 CI 状态：[Actions](https://github.com/YNA1823/JSONDemo/actions)

## 测试报告

```bash
mvn allure:serve
```

报告包含：
- 测试用例详情
- 请求/响应日志
- 测试步骤
- 失败信息

## 配置说明

```yaml
api:
  baseUrl: "https://jsonplaceholder.typicode.com"
  timeout: 10000

database:
  enabled: true
  url: "jdbc:mysql://localhost:3306/test_demo"
  username: "root"
  password: "123456"
```

## 项目亮点

1. **分层设计**：配置、模型、工具、测试分离，结构清晰
2. **数据驱动**：支持 Excel 和数据库两种数据源
3. **测试工具开发**：自主实现测试数据生成器，体现工具开发能力
4. **CI/CD 集成**：GitHub Actions 自动化测试流程
5. **数据库集成**：测试数据可从 MySQL 读取，测试结果自动入库
6. **日志完善**：控制台 + 文件双输出，便于排查问题

## 后续计划

- [ ] 添加失败重试机制
- [ ] 增加更多接口测试 (Users, Comments)
- [ ] 添加接口性能测试

## 联系方式

作者：YNA1823
日期：2024

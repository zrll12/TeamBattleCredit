# TeamBattleCredit 插件

一个基于团队积分系统的Minecraft Bukkit/Spigot插件，用于管理团队死亡惩罚和积分系统。

## 功能特点

- 基于LuckPerms组系统的团队积分管理
- 玩家死亡时自动扣除团队积分
- 支持基于时间的死亡惩罚规则配置
- 团队积分不足时禁止玩家复活
- 提供命令管理团队积分
- 支持PlaceholderAPI占位符

## 依赖

- [LuckPerms](https://luckperms.net/) - 用于团队(组)管理
- [KotlinMC](https://modrinth.com/plugin/kotlinmc) - Kotlin语言支持
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (可选) - 提供占位符支持

## 安装

1. 下载插件的最新版本
2. 将插件JAR文件放入服务器的`plugins`文件夹
3. 重启服务器或使用插件管理器加载插件
4. 配置`config.yml`文件

## 配置

插件首次运行后会生成默认配置文件。以下是配置文件的主要部分：

```yaml
# 默认积分值
defaultCredit: 1000

# 积分通知阈值配置
# 当队伍积分减少到以下阈值时，将向所有玩家发送通知
creditNotifications:
  # 格式: 积分阈值: 通知消息
  900: "&c[系统] &e{team} &f队伍积分已降至 &c{credit} &f点！"
  500: "&c[系统] &e{team} &f队伍积分已降至 &c{credit} &f点，请小心行动！"
  200: "&c[系统] &e{team} &f队伍积分已降至危险水平：&c{credit} &f点！"
  50: "&c[警告] &e{team} &f队伍积分即将耗尽：&c{credit} &f点！"

# 死亡惩罚规则
# 格式: 时间(秒): 扣除积分
deathCost:
  0: 1  # 默认扣除1点积分
  3600: 2  # 1小时后扣除2点积分
  7200: 3  # 2小时后扣除3点积分
```

### 积分通知配置说明

- 在`creditNotifications`部分，你可以配置多个积分阈值和对应的通知消息
- 当队伍积分降至或低于某个阈值时，系统会向所有在线玩家发送对应的通知消息
- 消息中可以使用以下占位符：
  - `{team}` - 队伍名称
  - `{credit}` - 当前积分
- 消息支持颜色代码（使用`&`符号）

## 命令

插件提供以下命令：

- `/credit reload` - 重新加载配置文件
- `/credit give <组名> <数量>` - 给指定组添加积分
- `/credit remove <组名> <数量>` - 从指定组移除积分

## 权限

- `teambattlecredit.command` - 允许使用/credit命令
- `teambattlecredit.reload` - 允许重新加载插件配置
- `teambattlecredit.give` - 允许给队伍添加积分
- `teambattlecredit.remove` - 允许从队伍移除积分

## PlaceholderAPI占位符

插件提供以下占位符：

- `%TeamBattleCredit_self_team%` - 显示玩家所在队伍
- `%TeamBattleCredit_self_credit%` - 显示玩家所在队伍的积分
- `%TeamBattleCredit_other_credit_<队伍名>%` - 显示指定队伍的积分

## 工作原理

1. 插件使用LuckPerms的组系统来识别玩家所属的团队
2. 当玩家死亡时，会根据配置的规则从团队积分中扣除相应的分数
3. 如果团队积分不足，玩家将无法复活
4. 管理员可以通过命令手动调整团队积分

## 注意事项

- 插件依赖LuckPerms进行团队管理，请确保正确配置LuckPerms
- 玩家必须属于一个LuckPerms组才能正常使用插件功能
- 建议在使用前先测试配置是否符合预期
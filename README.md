下载请点右边的release

前置插件:PacketEvents(必须), MythicMobs(可选), PlaceholderAPI(可选)

模仿原版药水机制的自定义药水插件，添加了一些药水效果：
- 愤怒(Anger): 每次攻击添加`等级`层`活力`效果
- 内燃(Combust): 每次伤害间隔造成`ceil(当前等级 + 当前等级 * log10(当前等级 + 1))`点着火(ON_FIRE)伤害，这会导致伤害增长速度极快
- 终焉(Death): 药水效果结束后将生物血量设置为0，倒计时10秒时会产生黑烟粒子和播放钟声，如果施加于玩家还会看到倒计时标题
- 雷霆(Lightning): 每次伤害间隔造成`等级`点雷电(LIGHTNING_BOLT)伤害并在目标位置生成雷电(仅视觉效果)
- 月华(Moonshine): 夜晚(游戏时间`<1000`刻或`>13000`刻)时攻击伤害增加`等级`点
- 剧毒(Poison): 每次伤害间隔造成`等级`点间接魔法(INDIRECT_MAGIC)伤害，每次伤害后等级-1，等级归零也会使效果失效。玩家血量会显示为中毒状态(变为黄绿心，仅视觉效果)
- 反射(Reflection): 每次受到攻击时对造成伤害者造成`等级`点荆棘(THORNS)伤害
- 破隐(Revealing): 对隐身生物多造成`等级`点伤害
- 逆转(Reverse): 受到的不超过`等级`的攻击伤害转化为回血
- 献祭(Sacrifice): 每秒失去`等级`点血量，攻击伤害增加`等级*round(最大血量/血量)`点
- 护盾(Shield): 受到伤害时，优先抵消护盾，每级可抵挡1点
- 旋转(Spin): 每刻旋转视角`等级`度
- 晴光(Sunshine): 白天(游戏时间`>1000`且`<13000`刻)时攻击伤害增加`等级`点
- 嘲讽(Taunt): 使周围生物目标强行指向自己，若装有MythicMobs插件，会临时将所在阵营替换为随机阵营，效果结束后换回
- 活力(Vigor): 攻击时增加`等级`点伤害

命令：
/customeffect modify <UUID/玩家名> <类型> [时长] [等级] [叠加状态/来源]
/customeffect modify <UUID/玩家名> <类型> [时长] [等级] [叠加状态] [来源]
/customeffect clear <UUID/玩家名> [类型]

叠加状态: 
- NORMAL(默认): 高等级效果会覆盖低等级效果，低等级效果隐藏，若低等级效果持续时间更长，高等级效果结束后低等级效果重新生效
- ADD_ALL: 将等级和持续时间叠加至原效果
- ADD_TIME: 将持续时间叠加至原效果
- ADD_AMPLIFIER: 将等级叠加至原效果
- REPLACE: 替换原效果

来源可为玩家名或UUID

PAPI变量：
%custompotion_<玩家名/实体UUID/me>_<药水ID>_<duration/level/multiplier/display/displayname/source/sourceuuid>%
me代表解析自己，玩家名支持下划线，做了专门处理
duration获取的是药水剩余时间(刻)
level和multiplier获取的都是药水等级
display和displayname获取的都是药水显示名
source获取的是来源实体显示名
sourceuuid获取的是来源实体uuid

配置文件：

config.yml:
```yaml
#Do message returned by command respect the sendCommandFeedback gamerule?
#命令返回的消息是否遵从sendCommandFeedback游戏规则?
respect_gamerule: true
```

message.yml:
```yaml
message:
  command:
    unknown_subcommand: "<red>未知子命令: %command%"
    unknown_effect: "<red>未知效果: %type%"
    invalid_number: "<red>等级和时长需要为有效整数"
    invalid_argument: "<red>无效参数"
    invalid_stacking_mode: "<red>无效的叠加状态，可用状态有: NORMAL, REPLACE, ADD_ALL, ADD_TIME, ADD_AMPLIFIER"
    0_args: "使用方法: /customeffect <modify|clear> ..."
    clear_2_args: "使用方法: /customeffect clear <UUID/玩家名> [类型]"
    modify_3_args_1: "使用方法: /customeffect modify <UUID/玩家名> <类型> [时长] [等级] [叠加状态/来源]"
    modify_3_args_2: "使用方法: /customeffect modify <UUID/玩家名> <类型> [时长] [等级] [叠加状态] [来源]"
    level_lower_than_0: "<red>等级需要大于0"
    duration_lower_than_0: "<red>时长需要大于0"
    not_found: "<red>未找到对应玩家或实体"
    applied: "已将%level%级%type%效果应用于%name%，持续%duration%刻"
    cleared: "已清除%name%的%type%效果"
    cleared_all: "已清除%name%的所有效果"

display-names:
  anger: "愤怒"
  combust: "内燃"
  death: "终焉"
  lightning: "雷霆"
  moonshine: "月华"
  poison: "剧毒"
  reflection: "反射"
  revealing: "破隐"
  reverse: "逆转"
  sacrifice: "血祭"
  shield: "护盾"
  spin: "旋转"
  sunshine: "晴光"
  taunt: "嘲讽"
  vigor: "活力"
```

API:
- 事件:
  - CustomPotionAddEvent
  - CustomPotionApplyEvent
  - CustomPotionRemoveEvent
- 管理类:
  - DatabaseManager: 用于管理玩家下线时储存的药水效果
  - PotionManager: 用于管理药水效果的添加/删除/生效等
具体方法等见源代码 

本来打算添加更多药水效果(大概66种)，不过由于将要使其兼容我自己的属性插件，要大改代码，就不继续更新新效果了，如果你有开发经验添加自定义效果应该很容易，你可以自己添加

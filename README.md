
# BadgeManager

[![](https://jitpack.io/v/charlie-captain/BadgeManager.svg)](https://jitpack.io/#charlie-captain/BadgeManager)

Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```


Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.charlie-captain:BadgeManager:1.0.1'
	}
```

## Usage


1. Create Badge Node

```
val nodeKey = "Badge"
BadgeManager.instance.createNewNode(nodeKey)
```

2. BadgeView bind Node

```
val badgeView = findViewById<BadgeView>(R.id.badge)
badgeView.bindNode(nodeKey)
```

3. Edit Badge Node
```
BadgeManager.instance.edit(nodeKey).setBadgeNumber(random).commit()
```


https://user-images.githubusercontent.com/18600419/153549438-54cd5680-9298-4ca9-8199-576a90ee1427.mov



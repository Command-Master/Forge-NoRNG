# Forge NoRNG

This is a java agent which removes most randomness from Minecraft.

NOTE: Fabric NoRNG is much easier to setup, and you should use it if it's available in your version

NOTE2: This agent was tested on Forge 1.12.2, so I can't guarantee it will work on other versions

## Installation
1. Clone the repository.
2. Find the full file path of norng.jar (for me it is `/home/commandmaster/Instrumentation/src/norng.jar`,
so this is what I will use in the explaination).
3. Go to .minecraft/versions and find the forge version you want to change
4. Inside that folder you should find a json file, open it.
5. There will be a segment saying `"logging": {}`
6. Change it to
```json
"logging": {
    "client": {
        "argument": "-javaagent:<full path of your norng.jar>",
        "file": {
            "id": "client-1.12.xml",
            "sha1": "ef4f57b922df243d0cef096efe808c72db042149",
            "size": 877,
            "url": "https://launcher.mojang.com/v1/objects/ef4f57b922df243d0cef096efe808c72db042149/client-1.12.xml"
        },
        "type": "log4j2-xml"
    }
}
```
And now if you will restart your launcher and run forge, it *should* have no randomness.
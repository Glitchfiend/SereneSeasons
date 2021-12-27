/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
var ASM = Java.type("net.minecraftforge.coremod.api.ASMAPI");

var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

var GET_BIOME = ASM.mapMethod("m_46857_");
var GET_BLOCK_STATE = ASM.mapMethod("m_8055_");

var COLD_ENOUGH_TO_SNOW = ASM.mapMethod("m_198904_");
var WARM_ENOUGH_TO_RAIN = ASM.mapMethod("m_198906_");
var SHOULD_SNOW_GOLEM_BURN = ASM.mapMethod("m_198910_");
var GET_PRECIPITATION = ASM.mapMethod("m_47530_");

function Transformation(name, desc) {
    this.name = name;
    this.desc = desc;
    this.funcs = [];

    for (var i = 2; i < arguments.length; i++) {
        this.funcs.push(arguments[i]);
    }
}

var TRANSFORMATIONS = {
    "net/minecraft/client/renderer/LevelRenderer": [ 
        new Transformation(ASM.mapMethod("m_109693_"), "(Lnet/minecraft/client/Camera;)V", patchWarmEnoughToRainCalls, patchLevelRendererGetPrecipitation),                   // tickRain
        new Transformation(ASM.mapMethod("m_109703_"), "(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", patchWarmEnoughToRainCalls, patchLevelRendererGetPrecipitation) // renderSnowAndRain
    ],
    "net/minecraft/world/entity/animal/SnowGolem": [ 
        new Transformation(ASM.mapMethod("m_8107_"), "()V", patchShouldSnowGolemBurnCalls) // aiStep
    ],
    "net/minecraft/world/level/biome/Biome": [ 
        new Transformation(ASM.mapMethod("m_47480_"), "(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", patchWarmEnoughToRainCalls), // shouldFreeze
        new Transformation(ASM.mapMethod("m_47519_"), "(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",  patchShouldSnow)             // shouldSnow
    ],
    "net/minecraft/server/level/ServerLevel": [
        new Transformation(ASM.mapMethod("m_8714_"), "(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", patchTickChunk) //tickChunk
    ],
    "net/minecraft/world/level/Level": [
        new Transformation(ASM.mapMethod("m_46758_"), "(Lnet/minecraft/core/BlockPos;)Z", patchIsRainingAt) //isRainingAt
    ]
};

function applyTransformations(classNode, method) {
    if (!(classNode.name in TRANSFORMATIONS)) {
        return;
    }

    var transformations = TRANSFORMATIONS[classNode.name];

    for (var i = 0; i < transformations.length; i++) {
        var transformation = transformations[i];

        if (transformation.name == method.name && transformation.desc == method.desc) {
            log("Transforming " + method.name + " " + method.desc + " in " + classNode.name);

            for each (var func in transformation.funcs) {
                func(method);
            }
            break;
        }
    }
}

function log(message) {
    print("[Serene Seasons Transformer]: " + message);
}

function initializeCoreMod() {
    return {
        "temperature_transformer": {
            "target": {
                "type": "CLASS",
                "names": function(listofclasses) { return Object.keys(TRANSFORMATIONS); }
            },
            "transformer": function(classNode) {
                for each (var method in classNode.methods) {
                    applyTransformations(classNode, method);
                }

                return classNode;
            }
        }
    };
}

function patchLevelRendererGetPrecipitation(node) {
    var call = ASM.findFirstMethodCall(node,
        ASM.MethodType.VIRTUAL,
        "net/minecraft/world/level/biome/Biome",
        GET_PRECIPITATION,
        "()Lnet/minecraft/world/level/biome/Biome$Precipitation;");

    if (call == null) {
        log("Failed to locate call to getPrecipitation");
        return;
    }

    node.instructions.insertBefore(call, ASM.buildMethodCall(
        "sereneseasons/season/SeasonHooks",
        "getLevelRendererPrecipitation",
        "(Lnet/minecraft/world/level/biome/Biome;)Lnet/minecraft/world/level/biome/Biome$Precipitation;",
        ASM.MethodType.STATIC
    ));
    node.instructions.remove(call);
    log("Successfully replaced getPrecipitation in " + node.name);
}

// This is used to make farmland wet during rain (amongst other things)
function patchIsRainingAt(node) {
    // Insert a call to SeasonHooks isRainingAt at the start of Level's isRainingAt
    var insns = new InsnList();
    insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
    insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
    insns.add(ASM.buildMethodCall(
        "sereneseasons/season/SeasonHooks",
        "isRainingAtHook",
        "(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
        ASM.MethodType.STATIC
    ));
    insns.add(new InsnNode(Opcodes.IRETURN));
    node.instructions.insertBefore(node.instructions.getFirst(), insns);
    log("Successfully patched isRainingAt");
}

function patchShouldSnow(node) {
    // Insert a call to SeasonHooks shouldSnow at the start of Biome's shouldSnow
    var insns = new InsnList();
    insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
    insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
    insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
    insns.add(ASM.buildMethodCall(
        "sereneseasons/season/SeasonHooks",
        "shouldSnowHook",
        "(Lnet/minecraft/world/level/biome/Biome;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
        ASM.MethodType.STATIC
    ));
    insns.add(new InsnNode(Opcodes.IRETURN));
    node.instructions.insertBefore(node.instructions.getFirst(), insns);
    log("Successfully patched shouldSnow");
}

// This is used to fill cauldrons with snow/rain during rain
function patchTickChunk(node) {
    var call = ASM.findFirstMethodCall(node,
        ASM.MethodType.VIRTUAL,
        "net/minecraft/world/level/biome/Biome",
        COLD_ENOUGH_TO_SNOW,
        "(Lnet/minecraft/core/BlockPos;)Z");

    if (call == null) {
        log("Failed to locate call to isColdEnoughToSnow");
        return;
    }

    // Swap the call to Vanilla's coldEnoughToSnow with ours
    var insns = new InsnList();
    insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
    insns.add(ASM.buildMethodCall(
        "sereneseasons/season/SeasonHooks",
        "coldEnoughToSnowHook",
        "(Lnet/minecraft/world/level/biome/Biome;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/LevelReader;)Z",
        ASM.MethodType.STATIC
    ));

    node.instructions.insertBefore(call, insns);
    node.instructions.remove(call);
    log("Successfully patched tickChunk");
}

function patchWarmEnoughToRainCalls(method) {
    patchTemperatureCalls(method, WARM_ENOUGH_TO_RAIN, "warmEnoughToRainHook");
}

function patchShouldSnowGolemBurnCalls(method) {
    patchTemperatureCalls(method, SHOULD_SNOW_GOLEM_BURN, "shouldSnowGolemBurnHook");
}

function patchTemperatureCalls(method, callMethodName, hookMethodName) {
    var startIndex = 0;
    var patchedCount = 0;

    while (true) {
        var call = ASM.findFirstMethodCallAfter(method,
            ASM.MethodType.VIRTUAL,
            "net/minecraft/world/level/biome/Biome",
            callMethodName,
            "(Lnet/minecraft/core/BlockPos;)Z",
            startIndex);

        if (call == null) {
            break;
        }

        startIndex = method.instructions.indexOf(call);

        // We can't reuse the same world instruction, we must make a clone each iteration
        var worldLoad = buildWorldLoad(method);

        if (worldLoad == null) {
            log('Failed to find level load in ' + method.name);
            return;
        }

        var newInstructions = new InsnList();
        newInstructions.add(worldLoad); // Pass the world as an argument to our hook
        newInstructions.add(ASM.buildMethodCall(
            "sereneseasons/season/SeasonHooks",
            hookMethodName,
            "(Lnet/minecraft/world/level/biome/Biome;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/LevelReader;)Z",
            ASM.MethodType.STATIC
        )); // Replace the existing call with ours

        method.instructions.insertBefore(call, newInstructions);
        method.instructions.remove(call);
        patchedCount++;
    }

    if (patchedCount == 0) {
        log('Failed to locate call to warmEnoughToRain in ' + method.name);
    } else {
        log('Patched ' + patchedCount + ' calls');
    }
}

function buildWorldLoad(method) {
    for (var i = method.instructions.size() - 1; i >= 0; i--) {
        var instruction = method.instructions.get(i);

        if (instruction.getOpcode() == Opcodes.GETFIELD && instruction.desc == "Lnet/minecraft/world/level/Level;") {
            var newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            newInstructions.add(instruction.clone(null));
            return newInstructions;
        }
    }

    // NOTE: This is potentially dangerous if the first call is executed after warmEnoughToRain.
    // However, it is non-trivial to check for this properly.

    var worldCall = ASM.findFirstMethodCall(method,
        ASM.MethodType.INTERFACE,
        "net/minecraft/world/level/LevelReader",
        GET_BIOME,
        "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome;");

    if (worldCall == null) {
        worldCall = ASM.findFirstMethodCall(method,
            ASM.MethodType.VIRTUAL,
            "net/minecraft/world/level/Level",
            GET_BIOME,
            "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome;");
    }

    if (worldCall == null) {
        worldCall = ASM.findFirstMethodCall(method,
            ASM.MethodType.INTERFACE,
            "net/minecraft/world/level/LevelReader",
            GET_BLOCK_STATE,
            "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;");
    }

    if (worldCall == null) {
        log('Failed to locate a level call!');
        return null;
    }

    var worldCallIndex = method.instructions.indexOf(worldCall);
    var posALoad = method.instructions.get(worldCallIndex - 1);
    var worldALoad = method.instructions.get(worldCallIndex - 2);

    if (worldALoad.getOpcode() == Opcodes.ALOAD && posALoad.getOpcode() == Opcodes.ALOAD) {
        return worldALoad.clone(null);
    }

    return null;
}
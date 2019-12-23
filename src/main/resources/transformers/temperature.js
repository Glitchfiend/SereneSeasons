var ASM = Java.type("net.minecraftforge.coremod.api.ASMAPI");

var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

var GET_TEMPERATURE_CACHED = ASM.mapMethod("func_225486_c");
var GET_BIOME = ASM.mapMethod("func_226691_t_");
var GET_BLOCK_STATE = ASM.mapMethod("func_180495_p");
var TRANSFORMATIONS = {
    "net.minecraft.block.CauldronBlock": [ ASM.mapMethod("func_176224_k") ], // fillWithRain
    "net.minecraft.client.renderer.WorldRenderer": [ ASM.mapMethod("func_228436_a_"), ASM.mapMethod("func_228438_a_") ], // addRainParticles, renderRainSnow
    "net.minecraft.entity.passive.SnowGolemEntity": [ ASM.mapMethod("func_70636_d")], // livingTick
    "net.minecraft.world.biome.Biome": [ ASM.mapMethod("func_201854_a"), ASM.mapMethod("func_201850_b") ] // doesWaterFreeze, doesSnowGenerate
    }

function log(message)
{
    print("[Serene Seasons Transformer]: " + message);
}

function initializeCoreMod()
{
    return {
        "temperature_transformer": {
            "target": {
                "type": "CLASS",
                "names": function(listofclasses) { return Object.keys(TRANSFORMATIONS); }
            },
            "transformer": function(classNode) {
                for each (var method in classNode.methods) {
                    var methodsToTransform = TRANSFORMATIONS[classNode.name.split('/').join('.')];

                    if (~methodsToTransform.indexOf(method.name)) {
                        log("Transforming " + method.name + " in " + classNode.name);
                        patchGetTemperatureCachedCalls(method);
                    }
                }

                return classNode;
            }
        }
    };
}

function patchGetTemperatureCachedCalls(method)
{
    var startIndex = 0;
    var patchedCount = 0;

    while (true) {
        var getTemperatureCachedCall = ASM.findFirstMethodCallAfter(method,
            ASM.MethodType.VIRTUAL,
            "net/minecraft/world/biome/Biome",
            GET_TEMPERATURE_CACHED,
            "(Lnet/minecraft/util/math/BlockPos;)F",
            startIndex);

        if (getTemperatureCachedCall == null) {
            break;
        }

        startIndex = method.instructions.indexOf(getTemperatureCachedCall);

        // We can't reuse the same world instruction, we must make a clone each iteration
        var worldLoad = buildWorldLoad(method);

        if (worldLoad == null) {
            log('Failed to find world load in ' + method.name);
            return;
        }

        var newInstructions = new InsnList();
        newInstructions.add(worldLoad); // Pass the world as an argument to our hook
        newInstructions.add(ASM.buildMethodCall(
            "sereneseasons/season/SeasonHooks",
            "getBiomeTemperatureCachedHook",
            "(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IWorldReader;)F",
            ASM.MethodType.STATIC
        )); // Replace the existing call with ours

        method.instructions.insertBefore(getTemperatureCachedCall, newInstructions);
        method.instructions.remove(getTemperatureCachedCall);
        patchedCount++;
    }

    if (patchedCount == 0) {
        log('Failed to locate call to getTemperatureCached in ' + method.name);
    } else {
        log('Patched ' + patchedCount + ' calls');
    }
}

function buildWorldLoad(method)
{
    for (var i = method.instructions.size() - 1; i >= 0; i--) {
        var instruction = method.instructions.get(i);

        if (instruction.getOpcode() == Opcodes.GETFIELD && instruction.desc == "Lnet/minecraft/world/World;") {
            var newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            newInstructions.add(instruction.clone(null));
            return newInstructions;
        }
    }

    // NOTE: This is potentially dangerous if the first call is executed after getTemperatureCached.
    // However, it is non-trivial to check for this properly.

    var worldCall = ASM.findFirstMethodCall(method,
        ASM.MethodType.INTERFACE,
        "net/minecraft/world/IWorldReader",
        GET_BIOME,
        "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;");

    if (worldCall == null) {
        worldCall = ASM.findFirstMethodCall(method,
            ASM.MethodType.VIRTUAL,
            "net/minecraft/world/World",
            GET_BIOME,
            "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;");
    }

    if (worldCall == null) {
        worldCall = ASM.findFirstMethodCall(method,
            ASM.MethodType.INTERFACE,
            "net/minecraft/world/IWorldReader",
            GET_BLOCK_STATE,
            "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;");
    }

    if (worldCall == null) {
        log('Failed to locate a world call!');
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
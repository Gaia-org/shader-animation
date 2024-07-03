package com.example.shader.shaders

import android.graphics.RuntimeShader

val noiseShader = RuntimeShader(
    """
    uniform float2 resolution;
    uniform shader uContent; 
    uniform float intensity;
    
    vec4 main( vec2 fragCoord )
    {
        vec2 uv = fragCoord/resolution.xy;
        
        // Check if pixel is inside viewport bounds
        if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
            return vec4(uContent.eval(fragCoord));
        }

        float mdf = -0.8 * intensity; // increase for noise amount 
        float noise = (fract(sin(dot(uv, vec2(12.9898,78.233)*2.0)) * 43758.5453));
        vec4 tex = vec4(uContent.eval(fragCoord));
        
        mdf *= 1.5;
        
        vec4 col = tex - noise * mdf;

        return col;
    }
    """.trimIndent()
)

val messShader = RuntimeShader(
    """
        uniform float2 resolution;
        uniform shader uContent; 
      
        float mod289(float x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float2 mod289(float2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float3 mod289(float3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float4 mod289(float4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        
        float permute(float x) { return mod289((34.0 * x + 1.0) * x); }
        float3 permute(float3 x) { return mod289((34.0 * x + 1.0) * x); }
        
        float4 permute(float4 x) {
          return mod289(((x * 34.0) + 1.0) * x);
        }
        
        vec4 noise2(vec2 uv) {
          vec4 n = vec4(fract(sin(dot(uv.xy, vec2(12.9898,78.233))) * 43758.5453));
          return vec4(n.x, n.y, n.z, n.w);
        }
        
        float snoise(float2 v) {
          const float4 C = float4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
          float2 i  = floor(v + dot(v, C.yy));
          float2 x0 = v - i + dot(i, C.xx);
          float2 i1;
          i1 = (x0.x > x0.y) ? float2(1.0, 0.0) : float2(0.0, 1.0);
          float4 x12 = x0.xyxy + C.xxzz;
          x12.xy -= i1;
          i = mod289(i);
          float3 p = permute(permute(i.y + float3(0.0, i1.y, 1.0)) + i.x + float3(0.0, i1.x, 1.0));
          float3 m = max(0.5 - float3(dot(x0, x0), dot(x12.xy, x12.xy), dot(x12.zw, x12.zw)), 0.0);
          m = m * m;
          m = m * m;
          float3 x = 2.0 * fract(p * C.www) - 1.0;
          float3 h = abs(x) - 0.5;
          float3 ox = floor(x + 0.5);
          float3 a0 = x - ox;
          m *= 1.79284291400159 - 0.85373472095314 * (a0 * a0 + h * h);
          float3 g;
          g.x = a0.x * x0.x + h.x * x0.y;
          g.yz = a0.yz * x12.xz + h.yz * x12.yw;
          return 130.0 * dot(m, g);
        }
        
        half4 main( vec2 fragCoord )  {
          vec2 uv = fragCoord.xy / resolution.xy;
        
            if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
                return uContent.eval(fragCoord);
            }
        
            half4 grain = half4(snoise(fragCoord * 0.1) * 0.1 + 0.5);
            half4 fiber = half4(snoise(uv * 20.0) * 0.1 + 0.5);
            half4 dots = half4(snoise(fragCoord * 0.03) * 0.1 + 0.5);
        
            half4 randomSpecs = half4(0.0, 0.0, 0.0, 0.0);
            if (fract(dots.x * 10.0) > 0.8) {
                randomSpecs = half4(0.1, 0.1, 0.1, 1.0);
            }
        
            half4 randomFibers = half4(0.0, 0.0, 0.0, 0.0);
            if (fract(fiber.y * 10.0 + uv.y * 10.0) > 0.95) {
                randomFibers = half4(0.2, 0.2, 0.2, 1.0);
            }
        
            half4 baseColor = uContent.eval(fragCoord);
            half4 combinedNoise = baseColor + grain * 0.05 + fiber * 0.1 + randomSpecs + randomFibers;
            return mix(baseColor, combinedNoise, 0.8);
        }
    """
)

val SketchingPaperTexture = RuntimeShader(
    """
        uniform float2 resolution;
        uniform shader uContent; 
        uniform float contrast1;
        uniform float contrast2;
        uniform float amount; // 0.15

        float mod289(float x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float2 mod289(float2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float3 mod289(float3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float4 mod289(float4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        
        float permute(float x) { return mod289((34.0 * x + 1.0) * x); }
        float3 permute(float3 x) { return mod289((34.0 * x + 1.0) * x); }
        
        float4 permute(float4 x) {
          return mod289(((x * 34.0) + 1.0) * x);
        }
        
        float snoise(float2 v) {
          const float4 C = float4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
          float2 i  = floor(v + dot(v, C.yy));
          float2 x0 = v - i + dot(i, C.xx);
          float2 i1;
          i1 = (x0.x > x0.y) ? float2(1.0, 0.0) : float2(0.0, 1.0);
          float4 x12 = x0.xyxy + C.xxzz;
          x12.xy -= i1;
          i = mod289(i);
          float3 p = permute(permute(i.y + float3(0.0, i1.y, 1.0)) + i.x + float3(0.0, i1.x, 1.0));
          float3 m = max(0.5 - float3(dot(x0, x0), dot(x12.xy, x12.xy), dot(x12.zw, x12.zw)), 0.0);
          m = m * m;
          m = m * m;
          float3 x = 2.0 * fract(p * C.www) - 1.0;
          float3 h = abs(x) - 0.5;
          float3 ox = floor(x + 0.5);
          float3 a0 = x - ox;
          m *= 1.79284291400159 - 0.85373472095314 * (a0 * a0 + h * h);
          float3 g;
          g.x = a0.x * x0.x + h.x * x0.y;
          g.yz = a0.yz * x12.xz + h.yz * x12.yw;
          return 130.0 * dot(m, g);
        }

        half4 main( vec2 fragCoord )  {
            vec2 uv = fragCoord.xy / resolution.xy;
            if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
                return uContent.eval(fragCoord);
            }
            half4 baseColor = uContent.eval(fragCoord);
        
            // Generate Simplex noise
            float noise = snoise(uv * 200.0) * 0.5 + 0.5;
            noise = pow(noise, contrast1); // Increase contrast
        
            // Create a dot pattern
            float dotPattern = (sin(uv.x * 800.0) * sin(uv.y * 800.0)) * 0.5 + 0.5;
            dotPattern = pow(dotPattern, contrast2); // Increase contrast
        
            // Combine the noise and dot pattern
            float combinedTexture = mix(noise, dotPattern, 0.6);
        
            // Apply the texture to the base color
            half4 outputColor = baseColor + half4(combinedTexture, combinedTexture, combinedTexture, 0.0) * amount;
        
            return outputColor;
        }
    """
)


/**
 * Messing around from other shaders here, plus ChatGPT!
 */
val PaperTexture = RuntimeShader(
    """
    uniform float2 resolution;
    uniform shader uContent; 
    uniform float grainIntensity; // 0.05
    uniform float fiberIntensity; // 0.5

    vec4 noise2(vec2 uv) {
      vec4 n = vec4(fract(sin(dot(uv.xy, vec2(12.9894234,78.23342343))) * 43758.5453));
      return vec4(n.x, n.y, n.z, n.w);
    }
    
    vec4 main( vec2 fragCoord )  {
      vec2 uv = fragCoord.xy / resolution.xy;
      
      // Check if pixel is inside viewport bounds
      if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
          return vec4(uContent.eval(fragCoord));
      }
      
      vec4 grain = vec4(noise2(uv * 12.0).r - 0.5);
      vec4 fiber = vec4(noise2(uv * 23.0).g - 0.5);
      vec4 dots = vec4(noise2(uv * 30.0).b - 0.5);
      
      vec4 randomSpecs = vec4(0.0, 0.0, 0.0, 0.0);
      if (fract(dots.x * 10.0) > 0.8) {
        randomSpecs = vec4(0.1, 0.1, 0.1, 1.0);
      }
      
      vec4 randomFibers = vec4(0.0, 0.0, 0.0, 0.0);
      if (fract(fiber.y * 10.0 + uv.y * 10.0) > 0.95) {
        randomFibers = vec4(0.2, 0.2, 0.2, 1.0);
      }
    
      float fiberThickness = fract(fiber.y * 20.0 + uv.y * 10.0) * 0.05 + 0.01;
      vec4 squigglyFibers = vec4(1.0, 1.0, 1.0, 0.0);
      if (fract(fiber.y * 40.0 + uv.y * 20.0) > 0.95) {
        squigglyFibers = vec4(1.0, 1.0, 1.0, fiberThickness);
      }
      
      return min(vec4(uContent.eval(fragCoord)) 
        + grain * grainIntensity
        + fiber * fiberIntensity
        + randomSpecs, squigglyFibers);
    }
""".trimIndent()
)
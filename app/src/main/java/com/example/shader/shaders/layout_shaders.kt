package com.example.shader.shaders

import android.graphics.RuntimeShader

// https://shaders.skia.org/?id=9dc5c7170e82d49c47a3ee20d679ad5bef45b5ca7e23c4327dd93b8d3101256f
val gridLayoutShader = RuntimeShader(
    """
        const half4 backgroundColor = half4(1);
        const half4 originColor = half4(0.75,0.75,0.75,1);
        const half4 xColor = half4(1.0,0.84,0.0,1);
        const half4 yColor = half4(0.0,0.34,0.72,1);
        const half4 gridColor = half4(0,0,0,1);

        half4 main( in vec2 fragCoord )
        {
            // ten grids across on shortest side
            float pitch = min(iResolution.x/10, iResolution.y/10);
            if (int(mod(fragCoord.x, pitch)) == 0 ||
                int(mod(fragCoord.y, pitch)) == 0) {
                  return gridColor;        
            } else {
                float gridY = fragCoord.x/pitch;
                float gridX = fragCoord.y/pitch;
                if (gridX < 1 && gridY < 1)
                  return originColor;
                else if (gridX < 1)
                  return xColor;
                else if (gridY < 1)
                  return yColor;
                else
                  return backgroundColor;
            }
        }
    """.trimIndent()
)
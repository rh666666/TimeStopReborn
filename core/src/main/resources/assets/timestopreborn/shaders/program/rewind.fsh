#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 OutSize;
uniform vec2 InSize;
uniform float time;
in vec2 texCoord;
out vec4 fragColor;

#define PI 3.14159265359

float sineClampedTimescale(float t, float offset, float clampMult) {
    return clamp(t * 1.5 + offset, 0.0, PI * clampMult);
}

void main() {
    vec2 t = InSize / OutSize;
    vec2 uv = (2.0 * texCoord - t.xy) / t.y;
    float timescale = time * 1.2;
    float shakyTime = time;
    vec2 textureUv = texCoord.xy / t.xy;
    vec2 shaky = vec2(sin(shakyTime * 100.0) / 100.0, cos(shakyTime * 50.0) / 100.0)
            * (sin(clamp(shakyTime * 1.5 + 0.15, 0.0, PI)) * 0.25);
    vec2 warpy = vec2(
            sin(uv.x * sineClampedTimescale(timescale, 0.0, 1.0)),
            sin(uv.y * sineClampedTimescale(timescale, 0.0, 1.0))
    ) * 0.1;
    float distanceFromCentre = smoothstep(
            0.25, 0.45,
            distance(uv, vec2(0.0)) + 1.0 - sin(sineClampedTimescale(timescale * 0.9, 0.0, 1.0)) * 3.0
    );

    fragColor = texture(
            DiffuseSampler,
            textureUv + shaky + warpy * (1.0 - sign(distanceFromCentre - 0.5))
    );
}

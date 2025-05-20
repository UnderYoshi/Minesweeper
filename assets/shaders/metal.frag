#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
varying vec3 v_worldPos;

uniform sampler2D u_texture;
uniform sampler2D u_normalMap;    // your per‑tile normal map

uniform vec3 u_lightPos;         // eg. (boardCenterX, boardCenterY, lightZ)
uniform vec3 u_cameraPos;        // can be (camX, camY, camZ)
uniform vec3 u_ambientColor;     // low ambient term
uniform vec3 u_lightColor;       // typically white
uniform vec3 u_specularColor;    // white specular
uniform float u_shininess;       // e.g. 32.0–128.0

void main() {
    // fetch albedo and normal
    vec3 albedo = texture2D(u_texture, v_texCoord).rgb;
    vec3 N = texture2D(u_normalMap, v_texCoord).rgb * 2.0 - 1.0;
    N = normalize(N);

    // light vector
    vec3 L = normalize(u_lightPos - v_worldPos);
    float NdotL = max(dot(N, L), 0.0);

    // diffuse
    vec3 diffuse = u_lightColor * albedo * NdotL;

    // view vector
    vec3 V = normalize(u_cameraPos - v_worldPos);
    // halfway vector
    vec3 H = normalize(L + V);
    float NdotH = max(dot(N, H), 0.0);
    vec3 spec = u_lightColor * u_specularColor * pow(NdotH, u_shininess);

    // ambient
    vec3 ambient = u_ambientColor * albedo;

    vec3 color = ambient + diffuse + spec;
    gl_FragColor = vec4(color, 1.0);
}
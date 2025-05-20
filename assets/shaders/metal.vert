attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec2 v_texCoord;
varying vec3 v_worldPos;

void main() {
    v_texCoord = a_texCoord0;
    // Compute world position of this vertex
    vec4 worldPos = u_projTrans * a_position;
    v_worldPos = worldPos.xyz;
    gl_Position = worldPos;
}
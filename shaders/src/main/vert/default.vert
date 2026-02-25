#version 460 core

layout (location = 0) in vec2 aPos;

uniform mat4 uProjection;
uniform mat4 uTransform;
uniform vec4 uColor;

out vec4 fColor;

void main()
{
    gl_Position = uProjection * uTransform * vec4(aPos, 0.0, 1.0);
    fColor = uColor;
}
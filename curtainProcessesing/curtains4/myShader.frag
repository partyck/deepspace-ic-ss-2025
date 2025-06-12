#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

void main() {
  vec2 uv = gl_FragCoord.xy / resolution;

  // Sample current pixel
  vec4 current = texture2D(texture, uv);

  // Sample neighbors horizontally for motion blur (train effect)
  vec4 blur = vec4(0.0);
  float total = 0.0;

  // Sample range
  for (int i = -5; i <= 5; i++) {
    float offset = float(i) / resolution.x * 30.0; // adjust 30.0 for streak length
    float weight = 1.0 - abs(float(i)) / 5.0;       // triangular weights
    blur += texture2D(texture, vec2(uv.x + offset, uv.y)) * weight;
    total += weight;
  }

  blur /= total;

  // Blend the original with the blur
  gl_FragColor = mix(current, blur, 0.85); // 0.85 controls how much "train" smear
}

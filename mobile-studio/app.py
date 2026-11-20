import subprocess
from pathlib import Path
import gradio as gr

ROOT = Path(__file__).resolve().parent
ENGINE = ROOT / "claude-video-effects"
SCRIPT = ENGINE / "scripts" / "fx.py"
SOURCE = "https://github.com/Vibha-Ramprakash/claude-video-effects.git"

def prepare_engine():
    if not SCRIPT.exists():
        subprocess.run(["git", "clone", "--depth", "1", SOURCE, str(ENGINE)], check=True)
    return SCRIPT.exists()

def get_effects():
    prepare_engine()
    p = subprocess.run(["python", str(SCRIPT), "--list"], capture_output=True, text=True, check=True)
    items = []
    for line in p.stdout.splitlines():
        parts = line.strip().split(None, 2)
        if len(parts) >= 2:
            name = parts[1]
            items.append((name, name))
    return items

def render(video, effect, start, end, text_value, quality):
    if not video:
        raise gr.Error("Pehle apni video upload karo.")
    prepare_engine()
    src = Path(video)
    out = ROOT / "renders"
    out.mkdir(exist_ok=True)
    dst = out / f"{src.stem}_{effect}_saji.mp4"
    width = 720 if quality == "Mobile 720p" else 1080
    cmd = ["python", str(SCRIPT), effect, str(src), str(dst),
           "--start", str(float(start)), "--end", str(float(end)), "--width", str(width)]
    if text_value and text_value.strip():
        cmd += ["--text", text_value.strip()]
    proc = subprocess.run(cmd, capture_output=True, text=True, timeout=1800)
    if proc.returncode != 0:
        msg = (proc.stderr or proc.stdout or "Render failed")[-3500:]
        raise gr.Error("Render nahi hua. Details: " + msg)
    return str(dst), "Render complete. Video check karke download karo."

with gr.Blocks(title="SAJI Mobile Video Effects Studio", theme=gr.themes.Soft()) as demo:
    gr.Markdown("# ✦ SAJI Video Effects Studio\nPhone browser se video upload karo, effect select karo aur cloud par render karo.")
    with gr.Group():
        video = gr.Video(label="Upload video", sources=["upload"])
        with gr.Row():
            effect = gr.Dropdown(choices=[], label="Effect", interactive=True)
            quality = gr.Dropdown(["Mobile 720p", "Full HD 1080p"], value="Mobile 720p", label="Output")
        with gr.Row():
            start = gr.Number(value=0, label="Start time (seconds)", precision=2)
            end = gr.Number(value=5, label="End time (seconds)", precision=2)
        words = gr.Textbox(label="Optional text for supported effects", placeholder="Example: DREAM BIG")
        run = gr.Button("✨ Render Effect", variant="primary")
    output = gr.Video(label="Your edited video")
    status = gr.Markdown()
    run.click(render, [video, effect, start, end, words, quality], [output, status])
    demo.load(lambda: gr.update(choices=get_effects(), value="orbit"), outputs=effect)

if __name__ == "__main__":
    demo.launch(server_name="0.0.0.0", server_port=7860, share=False)

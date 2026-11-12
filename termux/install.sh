#!/data/data/com.termux/files/usr/bin/sh
# Installs the `lily` command. Run from Termux:  sh termux/install.sh
set -e
here=$(cd "$(dirname "$0")" && pwd)
install -m 755 "$here/lily" "$PREFIX/bin/lily"

# Needed so LilyCode's Run button can start commands in Termux. It lets any app that holds
# the Run permission run commands as you, so it is only set after you say yes.
props="$HOME/.termux/termux.properties"
if ! grep -q '^allow-external-apps *= *true' "$props" 2>/dev/null; then
    printf 'Set allow-external-apps=true in termux.properties? [y/N] '
    read -r answer
    if [ "$answer" = y ]; then
        mkdir -p "$HOME/.termux"
        echo 'allow-external-apps=true' >> "$props"
        termux-reload-settings 2>/dev/null || true
    fi
fi
echo "Done. In LilyCode, add your Termux home folder as a project, then run: lily file.py"

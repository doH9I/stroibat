#!/bin/bash

# Static site verification script
echo "✅ Construction CRM Static Frontend"
echo "📁 Publishing directory: public/"
echo "🌐 No build process required - static files ready"

# Simple verification that files exist
if [ -d "public" ] && [ -f "public/index.html" ]; then
    echo "✅ Static files verified"
    exit 0
else
    echo "❌ Static files not found"
    exit 1
fi
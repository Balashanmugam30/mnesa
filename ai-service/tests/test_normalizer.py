from app.core.normalizer import normalize_html_content


SAMPLE_HTML = """
<!DOCTYPE html>
<html>
<head>
    <title>Google Summer of Code 2026</title>
    <meta name="description" content="Global online program focused on bringing new contributors into open source software development.">
    <meta property="og:title" content="GSoC 2026 - Open Source Internship Program">
    <meta property="og:site_name" content="Google Open Source">
    <link rel="canonical" href="https://summerofcode.withgoogle.com/">
    <script type="application/ld+json">
    {
        "@context": "https://schema.org",
        "@type": "Event",
        "name": "GSoC 2026 Contributor Applications",
        "startDate": "2026-03-15"
    }
    </script>
    <script>
        console.log("tracking script that should be removed");
    </script>
    <style>
        .ads { display: none; }
    </style>
</head>
<body>
    <header>
        <nav><a href="/home">Home</a><a href="/login">Sign In</a></nav>
    </header>
    <main>
        <h1>Contributor Applications Open</h1>
        <p>Google Summer of Code is an open source internship program for student developers.</p>
        <p>Deadline: Applications close on April 14, 2026 at 18:00 UTC.</p>
        <h2>Eligibility Requirements</h2>
        <ul>
            <li>Must be at least 18 years old upon registration.</li>
            <li>Eligible to work in your country of residence.</li>
        </ul>
        <p>Apply online at <a href="https://summerofcode.withgoogle.com/apply">Application Portal</a>.</p>
    </main>
    <footer>
        <p>Copyright 2026 Google LLC. All rights reserved.</p>
    </footer>
</body>
</html>
"""


def test_normalizer_extracts_metadata():
    norm = normalize_html_content(SAMPLE_HTML, "https://summerofcode.withgoogle.com")
    assert norm.title == "Google Summer of Code 2026"
    assert norm.og_title == "GSoC 2026 - Open Source Internship Program"
    assert norm.og_site_name == "Google Open Source"
    assert norm.canonical_url == "https://summerofcode.withgoogle.com/"
    assert len(norm.json_ld_data) == 1
    assert norm.json_ld_data[0]["@type"] == "Event"
    assert norm.source_domain == "summerofcode.withgoogle.com"


def test_normalizer_strips_scripts_and_styles():
    norm = normalize_html_content(SAMPLE_HTML)
    assert "tracking script" not in norm.clean_text
    assert "display: none" not in norm.clean_text


def test_normalizer_strips_nav_and_footer_boilerplate():
    norm = normalize_html_content(SAMPLE_HTML)
    assert "Sign In" not in norm.clean_text
    assert "Copyright 2026 Google LLC" not in norm.clean_text


def test_normalizer_preserves_headings_and_deadline_text():
    norm = normalize_html_content(SAMPLE_HTML)
    assert "Contributor Applications Open" in norm.clean_text
    assert "Applications close on April 14, 2026" in norm.clean_text
    assert "Must be at least 18 years old" in norm.clean_text

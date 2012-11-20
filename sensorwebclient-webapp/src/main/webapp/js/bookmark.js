function addBookmark() {
	if (window.sidebar) {
		window.sidebar.addPanel(document.title || location.href, location.href, "")
	} else {
		if (window.external) {
			window.external.AddFavorite(window.location.href, document.title || location.href)
		}
	}
}

function addBookmarkWithParametrisizedUrl(title, url) {
	if (window.sidebar) {
		window.sidebar.addPanel(title, url, "")
	} else {
		if (window.external) {
			window.external.AddFavorite(url, title)
		}
	}
};
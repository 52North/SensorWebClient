contains dependencies which are not migrated to maven dependency mechanism yet.

To run a successful maven test/compile/package/... you first have to install 
the libs into your local repository (normally located at ~/.m2). Then maven
will find the libs on its path.

--
$Last Updated: 2011-12-01, henning$
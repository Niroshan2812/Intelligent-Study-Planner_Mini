import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, BookOpen, Calendar, Clock, User } from 'lucide-react';
import styles from './Layout.module.css';

const Layout = ({ children }) => {
    const location = useLocation();

    const navItems = [
        { path: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
        { path: '/subjects', icon: BookOpen, label: 'Subjects' },
        { path: '/exams', icon: Calendar, label: 'Exams' },
        { path: '/availability', icon: Clock, label: 'Availability' },
        { path: '/schedule', icon: Calendar, label: 'Schedule' },
    ];

    return (
        <div className={styles.layout}>
            <aside className={styles.sidebar}>
                <div className={styles.logo}>
                    <h2>StudyPlanner</h2>
                </div>
                <nav className={styles.nav}>
                    {navItems.map((item) => {
                        const Icon = item.icon;
                        const isActive = location.pathname === item.path;
                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                className={`${styles.navItem} ${isActive ? styles.active : ''}`}
                            >
                                <Icon size={20} />
                                <span>{item.label}</span>
                            </Link>
                        );
                    })}
                </nav>
                <div className={styles.userProfile}>
                    <User size={20} />
                    <span>Student Profile</span>
                </div>
            </aside>
            <main className={styles.mainContent}>
                {children}
            </main>
        </div>
    );
};

export default Layout;

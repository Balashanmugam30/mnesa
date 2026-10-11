"use client";

import React, { createContext, useContext, useEffect, useState, useCallback } from "react";
import { api, User } from "@/lib/api-client";

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  isBackendWaking: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, fullName: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isBackendWaking, setIsBackendWaking] = useState<boolean>(false);

  const syncAuthState = useCallback(() => {
    const storedUser = api.getStoredUser();
    const token = api.getAccessToken();
    if (storedUser && token) {
      setUser(storedUser);
    } else {
      setUser(null);
    }
    setIsLoading(false);
  }, []);

  useEffect(() => {
    syncAuthState();

    api.setWakingCallback((waking) => {
      setIsBackendWaking(waking);
    });

    const handleAuthChanged = () => {
      syncAuthState();
    };

    window.addEventListener("mnesa-auth-changed", handleAuthChanged);
    return () => {
      window.removeEventListener("mnesa-auth-changed", handleAuthChanged);
    };
  }, [syncAuthState]);

  const login = async (email: string, password: string) => {
    setIsLoading(true);
    try {
      const data = await api.login(email, password);
      setUser(data.user);
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (email: string, password: string, fullName: string) => {
    setIsLoading(true);
    try {
      const data = await api.register(email, password, fullName);
      setUser(data.user);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    setIsLoading(true);
    try {
      await api.logout();
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  };

  const refreshUser = async () => {
    const storedUser = api.getStoredUser();
    if (storedUser) {
      setUser(storedUser);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        isBackendWaking,
        login,
        register,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

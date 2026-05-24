--
-- PostgreSQL database dump
--

\restrict gB8pU4QAYboYySqWact2l7RT0aj5YOQa4Yy3B2Ej44kvbn9RaZMs2kTPwqwydwt

-- Dumped from database version 16.10
-- Dumped by pg_dump version 16.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: set_updated_at(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.set_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: admin_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.admin_users (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    password_hash character varying(255) NOT NULL,
    name character varying(50),
    role character varying(50) DEFAULT 'ADMIN'::character varying NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_admin_users_role CHECK (((role)::text = ANY ((ARRAY['SUPER_ADMIN'::character varying, 'ADMIN'::character varying])::text[]))),
    CONSTRAINT ck_admin_users_status CHECK ((status = ANY (ARRAY[0, 1])))
);


--
-- Name: TABLE admin_users; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.admin_users IS '管理员表：保存后台登录用户';


--
-- Name: COLUMN admin_users.password_hash; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.admin_users.password_hash IS '密码哈希值，禁止明文保存密码';


--
-- Name: admin_users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.admin_users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: admin_users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.admin_users_id_seq OWNED BY public.admin_users.id;


--
-- Name: audit_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_logs (
    id bigint NOT NULL,
    admin_id bigint NOT NULL,
    target_type character varying(50) NOT NULL,
    target_id bigint NOT NULL,
    action character varying(50) NOT NULL,
    before_data jsonb,
    after_data jsonb,
    reason character varying(500),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_audit_logs_action CHECK (((action)::text = ANY ((ARRAY['CREATE'::character varying, 'UPDATE'::character varying, 'VOID'::character varying, 'RESTORE'::character varying, 'DELETE'::character varying, 'LOGIN'::character varying, 'EXPORT'::character varying])::text[])))
);


--
-- Name: TABLE audit_logs; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.audit_logs IS '审计日志表：保存管理员修改、作废、恢复、导出等操作';


--
-- Name: audit_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.audit_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: audit_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.audit_logs_id_seq OWNED BY public.audit_logs.id;


--
-- Name: daily_reports; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.daily_reports (
    id bigint NOT NULL,
    team_id bigint NOT NULL,
    report_date date NOT NULL,
    total_training_minutes integer DEFAULT 0,
    speech_training_minutes integer DEFAULT 0,
    summary character varying(1000),
    problems character varying(1000),
    next_plan character varying(1000),
    submitted_by character varying(50),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_daily_reports_speech_minutes CHECK ((speech_training_minutes >= 0)),
    CONSTRAINT ck_daily_reports_total_minutes CHECK ((total_training_minutes >= 0))
);


--
-- Name: TABLE daily_reports; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.daily_reports IS '每日总结表：保存队伍每天整体训练时长、演讲时长、总结和计划';


--
-- Name: daily_reports_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.daily_reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: daily_reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.daily_reports_id_seq OWNED BY public.daily_reports.id;


--
-- Name: issues; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issues (
    id bigint NOT NULL,
    team_id bigint NOT NULL,
    task_id bigint,
    member_id bigint,
    title character varying(100) NOT NULL,
    description character varying(1000),
    severity character varying(20) DEFAULT '中'::character varying NOT NULL,
    status character varying(20) DEFAULT '待处理'::character varying NOT NULL,
    solution character varying(1000),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    resolved_at timestamp without time zone,
    CONSTRAINT ck_issues_resolved_at CHECK ((((status)::text <> ALL ((ARRAY['已解决'::character varying, '已关闭'::character varying])::text[])) OR (resolved_at IS NOT NULL))),
    CONSTRAINT ck_issues_severity CHECK (((severity)::text = ANY ((ARRAY['低'::character varying, '中'::character varying, '高'::character varying])::text[]))),
    CONSTRAINT ck_issues_status CHECK (((status)::text = ANY ((ARRAY['待处理'::character varying, '处理中'::character varying, '已解决'::character varying, '已关闭'::character varying])::text[])))
);


--
-- Name: TABLE issues; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.issues IS '问题闭环表：保存训练过程中出现的问题、状态和解决方案';


--
-- Name: issues_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.issues_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: issues_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.issues_id_seq OWNED BY public.issues.id;


--
-- Name: members; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.members (
    id bigint NOT NULL,
    team_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    role character varying(50),
    sort_order integer DEFAULT 0,
    status smallint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_members_status CHECK ((status = ANY (ARRAY[0, 1])))
);


--
-- Name: TABLE members; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.members IS '队员表：保存每支队伍的成员名单';


--
-- Name: COLUMN members.role; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.members.role IS '角色，如队长、队员、演讲负责人、技术负责人、文档负责人';


--
-- Name: COLUMN members.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.members.status IS '1 启用，0 停用';


--
-- Name: members_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.members_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: members_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.members_id_seq OWNED BY public.members.id;


--
-- Name: skill_categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.skill_categories (
    id bigint NOT NULL,
    track_name character varying(100) NOT NULL,
    name character varying(100) NOT NULL,
    sort_order integer DEFAULT 0,
    status smallint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_skill_categories_status CHECK ((status = ANY (ARRAY[0, 1])))
);


--
-- Name: TABLE skill_categories; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.skill_categories IS '技能大类表：保存技能 Part，如技能实操、项目展示、文档编写';


--
-- Name: COLUMN skill_categories.track_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.skill_categories.track_name IS '所属赛道名称';


--
-- Name: COLUMN skill_categories.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.skill_categories.status IS '1 启用，0 停用';


--
-- Name: skill_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.skill_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: skill_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.skill_categories_id_seq OWNED BY public.skill_categories.id;


--
-- Name: skill_tasks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.skill_tasks (
    id bigint NOT NULL,
    category_id bigint NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(500),
    expected_minutes integer,
    difficulty_level smallint,
    score_weight numeric(6,2),
    sort_order integer DEFAULT 0,
    status smallint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_skill_tasks_difficulty_level CHECK (((difficulty_level IS NULL) OR ((difficulty_level >= 1) AND (difficulty_level <= 5)))),
    CONSTRAINT ck_skill_tasks_expected_minutes CHECK (((expected_minutes IS NULL) OR (expected_minutes >= 0))),
    CONSTRAINT ck_skill_tasks_score_weight CHECK (((score_weight IS NULL) OR (score_weight >= (0)::numeric))),
    CONSTRAINT ck_skill_tasks_status CHECK ((status = ANY (ARRAY[0, 1])))
);


--
-- Name: TABLE skill_tasks; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.skill_tasks IS '技能点表：保存具体训练任务，如 YOLO 模型调优、演讲模拟';


--
-- Name: COLUMN skill_tasks.expected_minutes; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.skill_tasks.expected_minutes IS '建议训练时长（分钟）';


--
-- Name: COLUMN skill_tasks.difficulty_level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.skill_tasks.difficulty_level IS '难度等级 1-5';


--
-- Name: COLUMN skill_tasks.score_weight; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.skill_tasks.score_weight IS '评分权重';


--
-- Name: COLUMN skill_tasks.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.skill_tasks.status IS '1 启用，0 停用';


--
-- Name: skill_tasks_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.skill_tasks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: skill_tasks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.skill_tasks_id_seq OWNED BY public.skill_tasks.id;


--
-- Name: teams; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.teams (
    id bigint NOT NULL,
    institution_name character varying(100) NOT NULL,
    team_name character varying(100) NOT NULL,
    track_name character varying(100) NOT NULL,
    login_code character varying(32) NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_teams_status CHECK ((status = ANY (ARRAY[0, 1])))
);


--
-- Name: TABLE teams; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.teams IS '队伍表：保存院校、队伍、赛道、邀请码等信息';


--
-- Name: COLUMN teams.login_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.teams.login_code IS '队伍邀请码，唯一，例如 CQSX0075';


--
-- Name: COLUMN teams.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.teams.status IS '1 启用，0 停用';


--
-- Name: teams_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.teams_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: teams_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.teams_id_seq OWNED BY public.teams.id;


--
-- Name: time_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.time_logs (
    id bigint NOT NULL,
    daily_report_id bigint,
    team_id bigint NOT NULL,
    member_id bigint NOT NULL,
    task_id bigint NOT NULL,
    record_date date NOT NULL,
    duration_minutes integer NOT NULL,
    progress_status character varying(32) NOT NULL,
    result_desc character varying(500),
    problem_desc character varying(500),
    need_support boolean DEFAULT false NOT NULL,
    score_self smallint,
    is_voided boolean DEFAULT false NOT NULL,
    void_reason character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_time_logs_duration CHECK (((duration_minutes > 0) AND (duration_minutes <= 1440))),
    CONSTRAINT ck_time_logs_progress_status CHECK (((progress_status)::text = ANY ((ARRAY['未开始'::character varying, '进行中'::character varying, '部分完成'::character varying, '已完成'::character varying, '已掌握'::character varying, '受阻'::character varying])::text[]))),
    CONSTRAINT ck_time_logs_score_self CHECK (((score_self IS NULL) OR ((score_self >= 1) AND (score_self <= 5)))),
    CONSTRAINT ck_time_logs_void_reason CHECK ((((is_voided = false) AND (void_reason IS NULL)) OR (is_voided = true)))
);


--
-- Name: TABLE time_logs; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.time_logs IS '工时明细表：核心业务表。队员端只能新增，修改/作废仅允许管理端完成。';


--
-- Name: COLUMN time_logs.duration_minutes; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.time_logs.duration_minutes IS '耗时，统一存分钟';


--
-- Name: COLUMN time_logs.progress_status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.time_logs.progress_status IS '完成状态：未开始/进行中/已完成/已掌握';


--
-- Name: COLUMN time_logs.is_voided; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.time_logs.is_voided IS '是否作废。业务上不建议物理删除记录';


--
-- Name: time_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.time_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: time_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.time_logs_id_seq OWNED BY public.time_logs.id;


--
-- Name: admin_users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin_users ALTER COLUMN id SET DEFAULT nextval('public.admin_users_id_seq'::regclass);


--
-- Name: audit_logs id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs ALTER COLUMN id SET DEFAULT nextval('public.audit_logs_id_seq'::regclass);


--
-- Name: daily_reports id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daily_reports ALTER COLUMN id SET DEFAULT nextval('public.daily_reports_id_seq'::regclass);


--
-- Name: issues id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issues ALTER COLUMN id SET DEFAULT nextval('public.issues_id_seq'::regclass);


--
-- Name: members id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.members ALTER COLUMN id SET DEFAULT nextval('public.members_id_seq'::regclass);


--
-- Name: skill_categories id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill_categories ALTER COLUMN id SET DEFAULT nextval('public.skill_categories_id_seq'::regclass);


--
-- Name: skill_tasks id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill_tasks ALTER COLUMN id SET DEFAULT nextval('public.skill_tasks_id_seq'::regclass);


--
-- Name: teams id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.teams ALTER COLUMN id SET DEFAULT nextval('public.teams_id_seq'::regclass);


--
-- Name: time_logs id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.time_logs ALTER COLUMN id SET DEFAULT nextval('public.time_logs_id_seq'::regclass);


--
-- Data for Name: admin_users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.admin_users (id, username, password_hash, name, role, status, created_at, updated_at) FROM stdin;
1	15129866071	$2a$10$pHZJkRXVLMnlvp/7QFqVS..rJY.0EZInMeH6PNmNTPfqJcZpXTlb2	系统管理员	SUPER_ADMIN	1	2026-05-20 09:26:24.837398	2026-05-21 01:07:32.986467
\.


--
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_logs (id, admin_id, target_type, target_id, action, before_data, after_data, reason, created_at) FROM stdin;
\.


--
-- Data for Name: daily_reports; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.daily_reports (id, team_id, report_date, total_training_minutes, speech_training_minutes, summary, problems, next_plan, submitted_by, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: issues; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.issues (id, team_id, task_id, member_id, title, description, severity, status, solution, created_at, updated_at, resolved_at) FROM stdin;
\.


--
-- Data for Name: members; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.members (id, team_id, name, role, sort_order, status, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: skill_categories; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.skill_categories (id, track_name, name, sort_order, status, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: skill_tasks; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.skill_tasks (id, category_id, name, description, expected_minutes, difficulty_level, score_weight, sort_order, status, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: teams; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.teams (id, institution_name, team_name, track_name, login_code, status, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: time_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.time_logs (id, daily_report_id, team_id, member_id, task_id, record_date, duration_minutes, progress_status, result_desc, problem_desc, need_support, score_self, is_voided, void_reason, created_at, updated_at) FROM stdin;
\.


--
-- Name: admin_users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.admin_users_id_seq', 1, true);


--
-- Name: audit_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.audit_logs_id_seq', 1, false);


--
-- Name: daily_reports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.daily_reports_id_seq', 1, false);


--
-- Name: issues_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.issues_id_seq', 1, false);


--
-- Name: members_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.members_id_seq', 1, false);


--
-- Name: skill_categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.skill_categories_id_seq', 1, false);


--
-- Name: skill_tasks_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.skill_tasks_id_seq', 1, false);


--
-- Name: teams_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.teams_id_seq', 1, false);


--
-- Name: time_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.time_logs_id_seq', 1, false);


--
-- Name: admin_users admin_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin_users
    ADD CONSTRAINT admin_users_pkey PRIMARY KEY (id);


--
-- Name: audit_logs audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);


--
-- Name: daily_reports daily_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daily_reports
    ADD CONSTRAINT daily_reports_pkey PRIMARY KEY (id);


--
-- Name: issues issues_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issues
    ADD CONSTRAINT issues_pkey PRIMARY KEY (id);


--
-- Name: members members_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT members_pkey PRIMARY KEY (id);


--
-- Name: skill_categories skill_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill_categories
    ADD CONSTRAINT skill_categories_pkey PRIMARY KEY (id);


--
-- Name: skill_tasks skill_tasks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill_tasks
    ADD CONSTRAINT skill_tasks_pkey PRIMARY KEY (id);


--
-- Name: teams teams_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT teams_pkey PRIMARY KEY (id);


--
-- Name: time_logs time_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.time_logs
    ADD CONSTRAINT time_logs_pkey PRIMARY KEY (id);


--
-- Name: admin_users uk_admin_users_username; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin_users
    ADD CONSTRAINT uk_admin_users_username UNIQUE (username);


--
-- Name: daily_reports uk_daily_reports_id_team_date; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daily_reports
    ADD CONSTRAINT uk_daily_reports_id_team_date UNIQUE (id, team_id, report_date);


--
-- Name: daily_reports uk_daily_reports_team_date; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daily_reports
    ADD CONSTRAINT uk_daily_reports_team_date UNIQUE (team_id, report_date);


--
-- Name: members uk_members_id_team; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT uk_members_id_team UNIQUE (id, team_id);


--
-- Name: members uk_members_team_name; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT uk_members_team_name UNIQUE (team_id, name);


--
-- Name: skill_categories uk_skill_categories_track_name; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill_categories
    ADD CONSTRAINT uk_skill_categories_track_name UNIQUE (track_name, name);


--
-- Name: skill_tasks uk_skill_tasks_category_name; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill_tasks
    ADD CONSTRAINT uk_skill_tasks_category_name UNIQUE (category_id, name);


--
-- Name: teams uk_teams_institution_team_track; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT uk_teams_institution_team_track UNIQUE (institution_name, team_name, track_name);


--
-- Name: teams uk_teams_login_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT uk_teams_login_code UNIQUE (login_code);


--
-- Name: idx_audit_logs_admin_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_logs_admin_time ON public.audit_logs USING btree (admin_id, created_at);


--
-- Name: idx_audit_logs_target; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_logs_target ON public.audit_logs USING btree (target_type, target_id);


--
-- Name: idx_daily_reports_team_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_daily_reports_team_date ON public.daily_reports USING btree (team_id, report_date);


--
-- Name: idx_issues_severity; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_issues_severity ON public.issues USING btree (severity);


--
-- Name: idx_issues_team_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_issues_team_status ON public.issues USING btree (team_id, status);


--
-- Name: idx_members_team_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_members_team_id ON public.members USING btree (team_id);


--
-- Name: idx_skill_categories_track_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_skill_categories_track_name ON public.skill_categories USING btree (track_name);


--
-- Name: idx_skill_tasks_category_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_skill_tasks_category_id ON public.skill_tasks USING btree (category_id);


--
-- Name: idx_time_logs_is_voided; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_time_logs_is_voided ON public.time_logs USING btree (is_voided);


--
-- Name: idx_time_logs_member_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_time_logs_member_date ON public.time_logs USING btree (member_id, record_date);


--
-- Name: idx_time_logs_need_support; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_time_logs_need_support ON public.time_logs USING btree (need_support) WHERE (need_support = true);


--
-- Name: idx_time_logs_task_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_time_logs_task_id ON public.time_logs USING btree (task_id);


--
-- Name: idx_time_logs_team_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_time_logs_team_date ON public.time_logs USING btree (team_id, record_date);


--
-- Name: admin_users trg_admin_users_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_admin_users_updated_at BEFORE UPDATE ON public.admin_users FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: daily_reports trg_daily_reports_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_daily_reports_updated_at BEFORE UPDATE ON public.daily_reports FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: issues trg_issues_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_issues_updated_at BEFORE UPDATE ON public.issues FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: members trg_members_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_members_updated_at BEFORE UPDATE ON public.members FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: skill_categories trg_skill_categories_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_skill_categories_updated_at BEFORE UPDATE ON public.skill_categories FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: skill_tasks trg_skill_tasks_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_skill_tasks_updated_at BEFORE UPDATE ON public.skill_tasks FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: teams trg_teams_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_teams_updated_at BEFORE UPDATE ON public.teams FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: time_logs trg_time_logs_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_time_logs_updated_at BEFORE UPDATE ON public.time_logs FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- Name: audit_logs fk_audit_logs_admin; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT fk_audit_logs_admin FOREIGN KEY (admin_id) REFERENCES public.admin_users(id);


--
-- Name: daily_reports fk_daily_reports_team; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daily_reports
    ADD CONSTRAINT fk_daily_reports_team FOREIGN KEY (team_id) REFERENCES public.teams(id);


--
-- Name: issues fk_issues_member_same_team; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issues
    ADD CONSTRAINT fk_issues_member_same_team FOREIGN KEY (member_id, team_id) REFERENCES public.members(id, team_id);


--
-- Name: issues fk_issues_task; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issues
    ADD CONSTRAINT fk_issues_task FOREIGN KEY (task_id) REFERENCES public.skill_tasks(id);


--
-- Name: issues fk_issues_team; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issues
    ADD CONSTRAINT fk_issues_team FOREIGN KEY (team_id) REFERENCES public.teams(id);


--
-- Name: members fk_members_team; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.members
    ADD CONSTRAINT fk_members_team FOREIGN KEY (team_id) REFERENCES public.teams(id);


--
-- Name: skill_tasks fk_skill_tasks_category; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill_tasks
    ADD CONSTRAINT fk_skill_tasks_category FOREIGN KEY (category_id) REFERENCES public.skill_categories(id);


--
-- Name: time_logs fk_time_logs_daily_report_same_team_date; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.time_logs
    ADD CONSTRAINT fk_time_logs_daily_report_same_team_date FOREIGN KEY (daily_report_id, team_id, record_date) REFERENCES public.daily_reports(id, team_id, report_date);


--
-- Name: time_logs fk_time_logs_member_same_team; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.time_logs
    ADD CONSTRAINT fk_time_logs_member_same_team FOREIGN KEY (member_id, team_id) REFERENCES public.members(id, team_id);


--
-- Name: time_logs fk_time_logs_task; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.time_logs
    ADD CONSTRAINT fk_time_logs_task FOREIGN KEY (task_id) REFERENCES public.skill_tasks(id);


--
-- Name: time_logs fk_time_logs_team; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.time_logs
    ADD CONSTRAINT fk_time_logs_team FOREIGN KEY (team_id) REFERENCES public.teams(id);


--
-- PostgreSQL database dump complete
--

\unrestrict gB8pU4QAYboYySqWact2l7RT0aj5YOQa4Yy3B2Ej44kvbn9RaZMs2kTPwqwydwt

